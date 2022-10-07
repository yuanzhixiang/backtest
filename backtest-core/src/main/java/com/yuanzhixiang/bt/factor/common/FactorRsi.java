package com.yuanzhixiang.bt.factor.common;

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
import com.yuanzhixiang.bt.factor.common.RealPriceFactor.RealPrice;
import com.yuanzhixiang.bt.kit.BigDecimalConstants;

/**
 * @author Yuan Zhixiang
 */
public class FactorRsi implements Factor {

    /**
     * Get the rsi value
     *
     * @param factors period
     * @param offset  the offset from the current bar position, which is less than or equal to 0
     * @return Rsi value
     */
    public static Rsi get(Factors factors, int offset) {
        return RSI.get(ContextLocal.get().getFactors(factors.getIdentity(), offset));
    }

    public static class Rsi {

        private final double rsi;

        private final Price previousUp;

        private final Price previousDown;

        public Rsi(double rsi, Price previousUp, Price previousDown) {
            this.rsi = rsi;
            this.previousUp = previousUp;
            this.previousDown = previousDown;
        }

        public double getRsi() {
            return rsi;
        }

        public Price getPreviousUp() {
            return previousUp;
        }

        public Price getPreviousDown() {
            return previousDown;
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // The logic of privatization

    private static final SuppliedLocal<Factors, Rsi> RSI = new SuppliedLocal<>();

    private static final Local<Factors, Rsi> VALUE = new Local<>();

    public FactorRsi(int duration) {
        this.duration = duration;
    }

    static Supplier<Rsi> getSupplier(ContextService contextService, Factors specific, int duration) {
        return () -> {
            Rsi rsiValue = VALUE.get(specific);
            if (rsiValue != null) {
                return rsiValue;
            }

            // If the previous rsi exists, use the value of the previous rsi to calculate the current rsi.
            Rsi pRsi = FactorRsi.get(specific, -1);
            if (pRsi != null) {
                RealPrice realPrice = RealPriceFactor.get(specific);
                RealPrice pRealPrice = RealPriceFactor.get(contextService.getFactors(specific.getIdentity(), -1));

                BigDecimal diff = valueOf(realPrice.getClose()).subtract(valueOf(pRealPrice.getClose()));

                BigDecimal up;
                BigDecimal down;
                // If there are no invalid calculations, the final data and Tongdsxin will not match.
                if (diff.doubleValue() > 0) {
                    double previousUp = RealPriceFactor.get(specific.getSymbol(), pRsi.getPreviousUp());
                    double previousDown = RealPriceFactor.get(specific.getSymbol(), pRsi.getPreviousDown());
                    up = valueOf(previousUp).multiply(valueOf(duration - 1)).add(diff)
                        .divide(valueOf(duration), 8, RoundingMode.HALF_UP);
                    down = valueOf(previousDown).multiply(valueOf(duration - 1).add(BigDecimal.ZERO))
                        .divide(valueOf(duration), 8, RoundingMode.HALF_UP);
                } else if (diff.doubleValue() < 0) {
                    double previousUp = RealPriceFactor.get(specific.getSymbol(), pRsi.getPreviousUp());
                    double previousDown = RealPriceFactor.get(specific.getSymbol(), pRsi.getPreviousDown());
                    up = valueOf(previousUp).multiply(valueOf(duration - 1).add(BigDecimal.ZERO))
                        .divide(valueOf(duration), 8, RoundingMode.HALF_UP);
                    down = valueOf(previousDown).multiply(valueOf(duration - 1)).add(diff.abs())
                        .divide(valueOf(duration), 8, RoundingMode.HALF_UP);
                } else {
                    double previousUp = RealPriceFactor.get(specific.getSymbol(), pRsi.getPreviousUp());
                    double previousDown = RealPriceFactor.get(specific.getSymbol(), pRsi.getPreviousDown());
                    up = valueOf(previousUp).multiply(valueOf(duration - 1).add(BigDecimal.ZERO))
                        .divide(valueOf(duration), 8, RoundingMode.HALF_UP);
                    down = valueOf(previousDown).multiply(valueOf(duration - 1).add(BigDecimal.ZERO))
                        .divide(valueOf(duration), 8, RoundingMode.HALF_UP);
                }

                BigDecimal rsi = up.divide(up.add(down), 4, RoundingMode.HALF_UP).multiply(BigDecimalConstants._100);

                BigDecimal adjustment = specific.getClose().getAdjustment();
                rsiValue = new Rsi(rsi.doubleValue(), new Price(up.doubleValue(), adjustment),
                    new Price(down.doubleValue(), adjustment));

                VALUE.set(specific, rsiValue);

                return rsiValue;
            }

            // If the previous rsi does not exist, try to use the average value to calculate rsi.
            BigDecimal up = BigDecimal.ZERO;
            BigDecimal down = BigDecimal.ZERO;
            for (int offset = duration * -1 + 1; offset <= 0; offset++) {
                Factors factor = contextService.getFactors(specific.getIdentity(), offset);
                Factors pFactor = contextService.getFactors(specific.getIdentity(), offset - 1);

                if (pFactor == null || factor == null) {
                    return null;
                }

                double close = RealPriceFactor.get(factor.getSymbol(), factor.getClose());
                double pClose = RealPriceFactor.get(pFactor.getSymbol(), pFactor.getClose());
                BigDecimal diff = valueOf(close).subtract(valueOf(pClose));

                if (diff.doubleValue() > 0) {
                    up = up.add(diff);
                } else {
                    down = down.add(diff.abs());
                }
            }

            up = up.divide(valueOf(duration), 8, RoundingMode.HALF_UP);
            down = down.divide(valueOf(duration), 8, RoundingMode.HALF_UP);
            BigDecimal rsi = up.divide(up.add(down), 8, RoundingMode.HALF_UP).multiply(BigDecimalConstants._100);

            BigDecimal adjustment = specific.getClose().getAdjustment();
            rsiValue = new Rsi(rsi.doubleValue(), new Price(up.doubleValue(), adjustment),
                new Price(down.doubleValue(), adjustment));
            VALUE.set(specific, rsiValue);

            return rsiValue;
        };
    }

    private final int duration;

    @Override
    public void bind(ContextService contextService, Factors factors) {
        RSI.setSupplier(factors, getSupplier(contextService, factors, duration));
        RSI.get(factors);
    }
}
