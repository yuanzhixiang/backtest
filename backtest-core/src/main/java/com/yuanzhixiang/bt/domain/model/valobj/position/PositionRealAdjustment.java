package com.yuanzhixiang.bt.domain.model.valobj.position;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.yuanzhixiang.bt.engine.Context;
import com.yuanzhixiang.bt.engine.ContextLocal;

/**
 * @author yuanzhixiang
 */
public class PositionRealAdjustment {

    public static BigDecimal get(Position position) {
        Context context = ContextLocal.get();
        BigDecimal adjustment = context.getFactors(position.getSymbol(), 0).getClose().getAdjustment();
        BigDecimal previousAdjustment = context.getFactors(position.getSymbol(), -1).getClose().getAdjustment();
        if (adjustment.equals(previousAdjustment)) {
            return BigDecimal.ONE;
        }

        return adjustment.divide(previousAdjustment, 8, RoundingMode.HALF_UP);
    }

}
