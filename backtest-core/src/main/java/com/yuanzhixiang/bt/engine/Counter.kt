package com.yuanzhixiang.bt.engine

import com.yuanzhixiang.bt.engine.domain.Symbol
import com.yuanzhixiang.bt.engine.domain.Position
import com.yuanzhixiang.bt.engine.domain.PendingOrder
import com.yuanzhixiang.bt.engine.domain.TradeRecord
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * @author Yuan Zhixiang
 */
interface Counter {
    fun buy(symbol: Symbol, tradeTime: LocalDateTime, price: BigDecimal, quantity: BigDecimal)
    fun sell(symbol: Symbol, tradeTime: LocalDateTime, price: BigDecimal, quantity: BigDecimal)

    fun cancel(orderId: Long)
    fun queryPendingOrders(symbol: Symbol): List<PendingOrder>

    fun queryBalance(): BigDecimal
    fun queryPosition(symbol: Symbol): Position
    fun queryTradeRecords(): List<TradeRecord>

    @Deprecated("")
    fun refresh() {
    }
}