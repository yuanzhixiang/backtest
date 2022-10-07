package com.yuanzhixiang.bt.engine.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.yuanzhixiang.bt.engine.domain.Position;
import com.yuanzhixiang.bt.service.ContextService;
import com.yuanzhixiang.bt.engine.ContextLocal;

/**
 * @author Yuan Zhixiang
 */
public class PositionRealAdjustment {

    public static BigDecimal get(Position position) {
        ContextService contextService = ContextLocal.get();
        BigDecimal adjustment = contextService.getFactors(position.getSymbol(), 0).getClose().getAdjustment();
        BigDecimal previousAdjustment = contextService.getFactors(position.getSymbol(), -1).getClose().getAdjustment();
        if (adjustment.equals(previousAdjustment)) {
            return BigDecimal.ONE;
        }

        return adjustment.divide(previousAdjustment, 8, RoundingMode.HALF_UP);
    }

}
