package com.yuanzhixiang.bt.service

import com.yuanzhixiang.bt.engine.domain.Symbol
import com.yuanzhixiang.bt.engine.domain.Position
import com.yuanzhixiang.bt.engine.domain.PositionRealAdjustment
import com.yuanzhixiang.bt.engine.BackTestException
import com.yuanzhixiang.bt.engine.Counter
import com.yuanzhixiang.bt.engine.SideEnum
import com.yuanzhixiang.bt.engine.domain.PendingOrder
import com.yuanzhixiang.bt.engine.domain.TradeRecord
import com.yuanzhixiang.bt.kit.SymbolUtil
import java.lang.UnsupportedOperationException
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.math.BigDecimal.valueOf
import java.math.RoundingMode
import java.time.LocalDateTime

/**
 * @author Yuan Zhixiang
 */
class CounterService(
    stampDutyRate: Double,
    transferFeeRate: Double,
    commissionRate: Double,
    balance: Double,
    todayAddOneTrade: Boolean
) : Counter {
    /**
     * 印花税
     */
    private val stampDutyRate: BigDecimal

    /**
     * 手续费
     */
    private val transferFeeRate: BigDecimal

    /**
     * 佣金费率
     */
    private val commissionRate: BigDecimal
    private val todayAddOneTrade: Boolean
    private var balance: BigDecimal

    private val positionMap: MutableMap<Symbol, Position> = HashMap()

    // todo If the delivery order records are stored in the memory, then a memory leak will be caused when the number
    //   of delivery orders is very large.
    private val tradeRecordList: MutableList<TradeRecord> = ArrayList()

    init {
        if (stampDutyRate < 0 || transferFeeRate < 0 || commissionRate < 0) {
            throw BackTestException("StampDutyRate, TransferFeeRate, CommissionRate must be great than 0.")
        }
        this.stampDutyRate = valueOf(stampDutyRate)
        this.transferFeeRate = valueOf(transferFeeRate)
        this.commissionRate = valueOf(commissionRate)
        this.balance = valueOf(balance)
        this.todayAddOneTrade = todayAddOneTrade
    }

    /**
     * Update position
     *
     * @param position position
     */
    fun putPosition(position: Position) {
        positionMap[position.symbol] = position
    }

    /**
     * Get position
     *
     * @param symbol symbol
     * @return position
     */
    override fun queryPosition(symbol: Symbol): Position {
        var position = positionMap[symbol]
        if (position == null) {
            position = Position(symbol, ZERO, ZERO, ZERO, ZERO, null)
            positionMap[symbol] = position
        }
        return position
    }

    override fun refresh() {
        for (position in positionMap.values) {
            if (position.totalQuantity == ZERO) {
                // Empty localMap if there is no quantity
                if (position.localMap != null) {
                    position.localMap = null
                }
                continue
            }
            val realAdjustment = PositionRealAdjustment.get(position)
            var newHoldQuantity = valueOf(position.quantity.toLong()).multiply(realAdjustment)
            var newFrozenQuantity = valueOf(position.frozenQuantity.toLong()).multiply(realAdjustment)
            newHoldQuantity = newHoldQuantity.add(newFrozenQuantity)
            newFrozenQuantity = ZERO
            val newBuyInFee = position.buyInFee.multiply(realAdjustment)
            val newCostPrice = position.costPrice.multiply(realAdjustment)
            putPosition(
                Position(
                    position.symbol,
                    newHoldQuantity,
                    newFrozenQuantity,
                    newBuyInFee,
                    newCostPrice,
                    position.localMap
                )
            )
        }
    }

    override fun queryBalance(): BigDecimal {
        return balance
    }

    override fun sell(symbol: Symbol, tradeTime: LocalDateTime, price: BigDecimal, quantity: BigDecimal) {
        val position = queryPosition(symbol)
        // calculate exchange cost
        var sellAmount = price * quantity
        val transferFee = getTransferFee(symbol, quantity)
        val commissionFee = getCommissionRate(sellAmount)
        val stampDuty = getStampDuty(sellAmount)

        // Expense deductions
        sellAmount = sellAmount.subtract(transferFee).subtract(commissionFee).subtract(stampDuty)
        val oldHoldQuantity = position.quantity
        val oldFrozenQuantity = position.frozenQuantity
        val oldTotalQuantity = oldHoldQuantity.add(oldFrozenQuantity)
        val oldBuyInFee = position.buyInFee
        val newHoldQuantity = oldHoldQuantity.subtract(quantity)
        val newTotalQuantity = newHoldQuantity.add(oldFrozenQuantity)
        val purchaseCost = oldBuyInFee.multiply(quantity).divide(oldTotalQuantity, 2, RoundingMode.HALF_UP)
        val profit = sellAmount.subtract(purchaseCost)
        val newBuyInFee = oldBuyInFee.subtract(purchaseCost)
        val newCostPrice = if (newTotalQuantity.toInt() == 0) ZERO else newBuyInFee.divide(
            newTotalQuantity,
            2,
            RoundingMode.HALF_UP
        )
        balance += sellAmount
        positionMap[symbol] = Position(
            symbol,
            newHoldQuantity,
            position.frozenQuantity,
            newBuyInFee,
            newCostPrice,
            position.localMap
        )
        tradeRecordList.add(
            TradeRecord(
                tradeTime,
                symbol, SideEnum.SELL,
                quantity.toDouble(),
                price.toDouble(),
                sellAmount.toDouble(),
                transferFee.toDouble(),
                commissionFee.toDouble(),
                stampDuty.toDouble(),
                profit.toDouble()
            )
        )
    }

    override fun cancel(orderId: Long) {
        throw UnsupportedOperationException()
    }

    override fun buy(symbol: Symbol, tradeTime: LocalDateTime, price: BigDecimal, quantity: BigDecimal) {
        // calculate exchange cost
        val amount = price * quantity
        val transferFee = getTransferFee(symbol, quantity)
        val commissionFee = getCommissionRate(amount)
        val deduct = amount.add(transferFee).add(commissionFee)

        // Calculate
        val balanceAfterPurchase = balance.subtract(deduct)
        if (balanceAfterPurchase.toDouble() < 0) {
            return
        }

        // Increase position
        val position = queryPosition(symbol)
        val oldHoldQuantity = position.quantity
        val oldFrozenQuantity = position.frozenQuantity
        val oldBuyInFee = position.buyInFee
        val newBuyInFee = oldBuyInFee.add(deduct)
        val newHoldQuantity = if (todayAddOneTrade) oldHoldQuantity else oldHoldQuantity.add(quantity)
        val newFrozenQuantity = if (todayAddOneTrade) oldFrozenQuantity.add(quantity) else oldFrozenQuantity
        val newCostPrice = newBuyInFee.divide(newHoldQuantity.add(newFrozenQuantity), 2, RoundingMode.HALF_UP)
        balance -= deduct
        positionMap[symbol] = Position(
            symbol, newHoldQuantity,
            newFrozenQuantity,
            newBuyInFee,
            newCostPrice,
            position.localMap
        )
        tradeRecordList.add(
            TradeRecord(
                tradeTime,
                symbol,
                SideEnum.BUY,
                quantity.toDouble(),
                price.toDouble(),
                deduct.toDouble(),
                transferFee.toDouble(),
                commissionFee.toDouble(),
                0.0,
                0.0
            )
        )
    }

    private fun getTransferFee(symbol: Symbol, quantity: BigDecimal): BigDecimal {
        return if (transferFeeRate.toDouble() != 0.0 && SymbolUtil.isSSE(symbol.code)) {
            quantity.multiply(transferFeeRate).setScale(2, RoundingMode.HALF_UP)
        } else {
            ZERO
        }
    }

    private fun getCommissionRate(amount: BigDecimal): BigDecimal {
        return if (commissionRate.toDouble() != 0.0) {
            amount.multiply(commissionRate).setScale(2, RoundingMode.HALF_UP)
        } else {
            ZERO
        }
    }

    private fun getStampDuty(amount: BigDecimal): BigDecimal {
        return stampDutyRate.multiply(amount).setScale(2, RoundingMode.HALF_UP)
    }

    override fun queryTradeRecords(): List<TradeRecord> {
        return tradeRecordList
    }

    override fun queryPendingOrders(symbol: Symbol): List<PendingOrder> {
        throw UnsupportedOperationException()
    }
}