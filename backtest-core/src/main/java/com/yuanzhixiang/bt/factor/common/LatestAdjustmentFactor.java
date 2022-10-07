package com.yuanzhixiang.bt.factor.common;

import java.math.BigDecimal;

import com.yuanzhixiang.bt.engine.domain.Symbol;
import com.yuanzhixiang.bt.engine.ContextLocal;

/**
 * @author Yuan Zhixiang
 */
public class LatestAdjustmentFactor {

    public static BigDecimal get(Symbol symbol) {
        return ContextLocal.get().getFactors(symbol, 0).getClose().getAdjustment();
    }
}
