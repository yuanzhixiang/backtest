package com.yuanzhixiang.bt.factor.common;

import java.math.BigDecimal;

import com.yuanzhixiang.bt.domain.model.valobj.Symbol;
import com.yuanzhixiang.bt.engine.ContextLocal;

/**
 * @author yuanzhixiang
 */
public class LatestAdjustmentFactor {

    public static BigDecimal get(Symbol symbol) {
        return ContextLocal.get().getFactors(symbol, 0).getClose().getAdjustment();
    }
}
