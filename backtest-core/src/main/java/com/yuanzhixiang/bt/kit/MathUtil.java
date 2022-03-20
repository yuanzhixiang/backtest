package com.yuanzhixiang.bt.kit;

import java.math.BigDecimal;

/**
 * @author yuanzhixiang
 */
public class MathUtil {

    public static double formatPercent(double num) {
        return BigDecimal.valueOf(num).multiply(BigDecimal.valueOf(100)).doubleValue();
    }

}
