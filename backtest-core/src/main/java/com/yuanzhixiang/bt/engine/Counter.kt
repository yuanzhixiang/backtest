package com.yuanzhixiang.bt.engine

import com.yuanzhixiang.bt.engine.domain.Symbol
import com.yuanzhixiang.bt.engine.domain.Position
import com.yuanzhixiang.bt.engine.domain.PendingOrder
import com.yuanzhixiang.bt.engine.domain.TradeRecord
import java.time.LocalDateTime

/**
 * @author Yuan Zhixiang
 */
interface Counter {
    fun buy(symbol: Symbol, tradeTime: LocalDateTime, price: Double, quantity: Double)
    fun sell(symbol: Symbol, tradeTime: LocalDateTime, price: Double, quantity: Double)

    fun cancel(orderId: Long)
    fun queryPendingOrders(symbol: Symbol): List<PendingOrder>

    fun queryBalance(): Double
    fun queryPosition(symbol: Symbol): Position
    fun queryTradeRecords(): List<TradeRecord>

    @Deprecated("")
    fun refresh() {
    }
}