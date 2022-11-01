package com.yuanzhixiang.bt.factor.common;

import static com.yuanzhixiang.bt.kit.BigDecimalConstants._10;
import static com.yuanzhixiang.bt.kit.BigDecimalConstants._11;
import static com.yuanzhixiang.bt.kit.BigDecimalConstants._13;
import static com.yuanzhixiang.bt.kit.BigDecimalConstants._2;
import static com.yuanzhixiang.bt.kit.BigDecimalConstants._25;
import static com.yuanzhixiang.bt.kit.BigDecimalConstants._27;
import static com.yuanzhixiang.bt.kit.BigDecimalConstants._8;
import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Supplier;

import com.yuanzhixiang.bt.engine.domain.Factors;
import com.yuanzhixiang.bt.engine.domain.Price;
import com.yuanzhixiang.bt.service.ContextService;
import com.yuanzhixiang.bt.engine.ContextLocal;
import com.yuanzhixiang.bt.engine.Local;
import com.yuanzhixiang.bt.engine.Local.SuppliedLocal;

/**
 * @author Yuan Zhixiang
 */
public class FactorMacd implements Factor<FactorMacd> {

    // Public static methods

    public static Macd get(Factors factors, int offset) {
        return MACD.get(ContextLocal.get().getFactors(factors.getIdentity(), offset));
    }

    /**
     * Macd fast and slow lines show a golden cross.
     *
     * @param factors period
     * @return Golden cross
     */
    public static boolean goldenCross(Factors factors) {
        Macd pMacd = get(factors, -1);
        Macd ppMacd = get(factors, -2);
        return pMacd != null && ppMacd != null && ppMacd.getDiff() <= ppMacd.getDea()
            && pMacd.getDiff() > pMacd.getDea();
    }

    /**
     * Macd fast and slow lines show a dead cross.
     *
     * @param factors period
     * @return Dead cross
     */
    public static boolean deadCross(Factors factors) {
        Macd pMacd = get(factors, -1);
        Macd ppMacd = get(factors, -2);
        return pMacd != null && ppMacd != null && ppMacd.getDiff() >= ppMacd.getDea()
            && pMacd.getDiff() < pMacd.getDea();
    }

    public static class Macd {

        BigDecimal originalEmaFast;

        BigDecimal originalEmaSlow;

        BigDecimal originalDiff;

        BigDecimal originalDea;

        BigDecimal originalBar;

        /**
         * Fast line
         */
        double diff;

        /**
         * Slow line
         */
        double dea;

        double bar;

        public double getDiff() {
            return diff;
        }

        public double getDea() {
            return dea;
        }

        public double getBar() {
            return bar;
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // The logic of privatization

    private static final SuppliedLocal<Factors, Macd> MACD = new SuppliedLocal<>();

    private static final Local<Factors, Macd> ORIGINAL_MACD = new Local<>();

    private static final int WEIGHTS_SCALE = 8;

    private static final BigDecimal EMA_FAST_PREVIOUS_WEIGHTS = _11.divide(_13, WEIGHTS_SCALE, RoundingMode.HALF_UP);
    private static final BigDecimal EMA_FAST_WEIGHTS = _2.divide(_13, WEIGHTS_SCALE, RoundingMode.HALF_UP);

    private static final BigDecimal EMA_SLOW_PREVIOUS_WEIGHTS = _25.divide(_27, WEIGHTS_SCALE, RoundingMode.HALF_UP);
    private static final BigDecimal EMA_SLOW_WEIGHTS = _2.divide(_27, WEIGHTS_SCALE, RoundingMode.HALF_UP);

    private static final BigDecimal DEA_PREVIOUS_WEIGHTS = _8.divide(_10, WEIGHTS_SCALE, RoundingMode.HALF_UP);
    private static final BigDecimal DEA_WEIGHTS = _2.divide(_10, WEIGHTS_SCALE, RoundingMode.HALF_UP);

    // todo Extract controllable calculation parameters
    static Supplier<Macd> getSupplier(ContextService contextService, Factors specific) {
        return () -> {
            Macd macd = ORIGINAL_MACD.get(specific);
            if (macd != null) {
                BigDecimal latestAdjustment = LatestAdjustmentFactor.get(specific.getSymbol());
                macd.diff = macd.originalDiff.divide(latestAdjustment, 2, RoundingMode.HALF_UP).doubleValue();
                macd.dea = macd.originalDea.divide(latestAdjustment, 2, RoundingMode.HALF_UP).doubleValue();
                macd.bar = macd.originalBar.divide(latestAdjustment, 2, RoundingMode.HALF_UP).doubleValue();
                return macd;
            }

            Factors previousFactors = contextService.getFactors(specific.getIdentity(), -1);
            Price priceClose = specific.getClose();

            // If there is no previous factor, indicating that this is the first factor,
            // then initialize the first node.
            if (previousFactors == null) {
                macd = new Macd();
                macd.originalEmaFast = priceClose.getPrice();
                macd.originalEmaSlow = priceClose.getPrice();
                macd.originalDiff = ZERO;
                macd.originalDea = ZERO;
                macd.originalBar = ZERO;
                macd.diff = 0;
                macd.dea = 0;
                macd.bar = 0;
                ORIGINAL_MACD.set(specific, macd);
                return macd;
            }

            // Calculate diff, dea, bar
            BigDecimal close = priceClose.getPrice().multiply(priceClose.getAdjustment());
            Macd previousMacd = MACD.get(previousFactors);
            BigDecimal emaFast = previousMacd.originalEmaFast.multiply(EMA_FAST_PREVIOUS_WEIGHTS).add(close.multiply(EMA_FAST_WEIGHTS)).setScale(4, RoundingMode.HALF_UP);
            BigDecimal emaSlow = previousMacd.originalEmaSlow.multiply(EMA_SLOW_PREVIOUS_WEIGHTS).add(close.multiply(EMA_SLOW_WEIGHTS)).setScale(4, RoundingMode.HALF_UP);
            BigDecimal diff = emaFast.subtract(emaSlow);
            BigDecimal dea = previousMacd.originalDea.multiply(DEA_PREVIOUS_WEIGHTS).add(diff.multiply(DEA_WEIGHTS)).setScale(4, RoundingMode.HALF_UP);
            BigDecimal bar = _2.multiply(diff.subtract(dea));

            // Save macd
            macd = new Macd();
            macd.originalEmaFast = emaFast;
            macd.originalEmaSlow = emaSlow;
            macd.originalDiff = diff;
            macd.originalDea = dea;
            macd.originalBar = bar;
            BigDecimal latestAdjustment = LatestAdjustmentFactor.get(specific.getSymbol());
            macd.diff = diff.divide(latestAdjustment, 2, RoundingMode.HALF_UP).doubleValue();
            macd.dea = dea.divide(latestAdjustment, 2, RoundingMode.HALF_UP).doubleValue();
            macd.bar = bar.divide(latestAdjustment, 2, RoundingMode.HALF_UP).doubleValue();
            ORIGINAL_MACD.set(specific, macd);
            return macd;
        };
    }

    @Override
    public void bind(ContextService contextService, Factors factors) {
        MACD.setSupplier(factors, getSupplier(contextService, factors));
        MACD.get(factors);
    }
}
