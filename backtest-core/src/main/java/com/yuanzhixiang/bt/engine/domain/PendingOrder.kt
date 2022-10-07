package com.yuanzhixiang.bt.engine.domain

import com.yuanzhixiang.bt.engine.SideEnum
import java.time.LocalDateTime

/**
 * @author Yuan Zhixiang
 */
data class PendingOrder(
    val id: Long,
    val symbol: Symbol,
    val quantity: Double,
    val price: Price,
    val side: SideEnum,
    val completedQuantity: Double,
    val gmtCreate: LocalDateTime,
)