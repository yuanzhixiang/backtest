package com.yuanzhixiang.bt.example.kit;

import com.yuanzhixiang.bt.engine.BackTestException;

import java.math.RoundingMode;

import static java.math.BigDecimal.valueOf;

/**
 * @author Yuan Zhixiang
 */
public class AccountKit {

    /**
     * Calculate quantity
     *
     * @param price   buy price
     * @param percent percent of position, range in (0, 1]
     * @return calculate buy quantity
     */
    public static double buy(double balance, double price, double percent) {
        if (percent > 1 || percent <= 0) {
            throw new BackTestException("Percentage must be between (0, 1]");
        }

        // Calculate the quantity that can be bought
        int quantity = valueOf(balance)
                .multiply(valueOf(percent))
                .divide(valueOf(price), 0, RoundingMode.DOWN)
                .intValue();
        quantity = (quantity / 100) * 100;

        return quantity;
    }

}
