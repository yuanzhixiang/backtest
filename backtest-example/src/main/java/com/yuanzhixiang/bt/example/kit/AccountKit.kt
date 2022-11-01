package com.yuanzhixiang.bt.example.kit

import com.yuanzhixiang.bt.engine.BackTestException
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * @author Yuan Zhixiang
 */
object AccountKit {
    /**
     * Calculate quantity
     *
     * @param price   buy price
     * @param percent percent of position, range in (0, 1]
     * @return calculate buy quantity
     */
    @JvmStatic
    fun buy(balance: BigDecimal, price: BigDecimal, percent: BigDecimal): BigDecimal {
        if (percent > BigDecimal.ONE || percent <= BigDecimal.ZERO) {
            throw BackTestException("Percentage must be between (0, 1]")
        }

        // Calculate the quantity that can be bought
        var quantity: Int = balance
            .multiply(percent)
            .divide(price, 0, RoundingMode.DOWN)
            .toInt()
        quantity = quantity / 100 * 100
        return quantity.toBigDecimal()
    }
}