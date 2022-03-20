package com.yuanzhixiang.bt.factor.common;

import static com.yuanzhixiang.bt.kit.BigDecimalConstants._1;
import static com.yuanzhixiang.bt.kit.BigDecimalConstants._100;
import static com.yuanzhixiang.bt.kit.BigDecimalConstants._2;
import static com.yuanzhixiang.bt.kit.BigDecimalConstants._3;
import static com.yuanzhixiang.bt.kit.BigDecimalConstants._50;
import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Supplier;

import com.yuanzhixiang.bt.domain.model.valobj.Price;
import com.yuanzhixiang.bt.engine.Context;
import com.yuanzhixiang.bt.engine.ContextLocal;
import com.yuanzhixiang.bt.engine.Local;
import com.yuanzhixiang.bt.engine.Local.SuppliedLocal;
import com.yuanzhixiang.bt.factor.common.Factor;
import com.yuanzhixiang.bt.domain.model.valobj.Factors;

/**
 * @author yuanzhixiang
 */
public class FactorKdj implements Factor {

    // Public static methods

    public static Kdj get(Factors factors, int offset) {
        return KDJ.get(ContextLocal.get().getFactors(factors.getIdentity(), offset));
    }

    public static boolean goldenCross(Factors factors) {
        Kdj pKdj = get(factors, -1);
        Kdj ppKdj = get(factors, -2);
        return pKdj != null && ppKdj != null && ppKdj.getK() <= ppKdj.getD() && pKdj.getK() > pKdj.getD();
    }

    public static boolean deadCross(Factors factors) {
        Kdj pKdj = get(factors, -1);
        Kdj ppKdj = get(factors, -2);
        return pKdj != null && ppKdj != null && ppKdj.getK() >= ppKdj.getD() && pKdj.getK() < pKdj.getD();
    }

    public static class Kdj {

        BigDecimal originalK;

        BigDecimal originalD;

        private double k;

        private double d;

        private double j;

        public double getK() {
            return k;
        }

        void setK(double k) {
            this.k = k;
        }

        public double getD() {
            return d;
        }

        void setD(double d) {
            this.d = d;
        }

        public double getJ() {
            return j;
        }

        void setJ(double j) {
            this.j = j;
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // The logic of privatization

    private static final SuppliedLocal<Factors, Kdj> KDJ = new SuppliedLocal<>();

    private static final Local<Factors, Kdj> ORIGINAL_VALUE = new Local<>();

    private static final BigDecimal PREVIOUS_WEIGHTS = _2.divide(_3, 8, RoundingMode.HALF_UP);
    private static final BigDecimal CURRENT_WEIGHTS = _1.divide(_3, 8, RoundingMode.HALF_UP);

    private static Supplier<Kdj> getSupplier(Context context, Factors specific, int range) {
        return () -> {
            Kdj kdj = ORIGINAL_VALUE.get(specific);
            if (kdj != null) {
                return kdj;
            }

            Factors previousFactors = context.getFactors(specific.getIdentity(), -1);

            // If there is no previous factor, indicating that this is the first factor,
            // then initialize the first node.
            if (previousFactors == null) {
                kdj = new Kdj();
                kdj.originalK = _50;
                kdj.originalD = _50;
                kdj.setK(kdj.originalK.setScale(2, RoundingMode.HALF_UP).doubleValue());
                kdj.setD(kdj.originalD.setScale(2, RoundingMode.HALF_UP).doubleValue());
                kdj.setJ(_50.setScale(2, RoundingMode.HALF_UP).doubleValue());
                ORIGINAL_VALUE.set(specific, kdj);
                return kdj;
            }

            // Find the highest and lowest price in the range.
            double maximumPrice = Double.MIN_VALUE;
            double lowestPrice = Double.MAX_VALUE;
            for (int offset = range * -1 + 1; offset <= 0; offset++) {
                Factors factors = context.getFactors(specific.getIdentity(), offset);
                if (factors == null) {
                    break;
                }
                Price highPrice = factors.getHigh();
                double high = valueOf(highPrice.getPrice()).multiply(highPrice.getAdjustment()).doubleValue();
                if (maximumPrice - high < 0) {
                    maximumPrice = high;
                }

                Price lowPrice = factors.getLow();
                double low = valueOf(lowPrice.getPrice()).multiply(lowPrice.getAdjustment()).doubleValue();
                if (lowestPrice - low > 0) {
                    lowestPrice = low;
                }
            }

            // Calculate rsv.
            Price closePrice = specific.getClose();
            BigDecimal close = valueOf(closePrice.getPrice()).multiply(closePrice.getAdjustment());
            BigDecimal rsv = close.subtract(valueOf(lowestPrice))
                .divide(valueOf(maximumPrice).subtract(valueOf(lowestPrice)), 4, RoundingMode.HALF_UP)
                .multiply(_100);

            // Calculate k, d, j.
            Kdj previousKdj = KDJ.get(previousFactors);
            BigDecimal k = previousKdj.originalK.multiply(PREVIOUS_WEIGHTS).add(rsv.multiply(CURRENT_WEIGHTS)).setScale(4, RoundingMode.HALF_UP);;
            BigDecimal d = previousKdj.originalD.multiply(PREVIOUS_WEIGHTS).add(k.multiply(CURRENT_WEIGHTS)).setScale(4, RoundingMode.HALF_UP);;
            BigDecimal j = k.multiply(_3).subtract(d.multiply(_2));

            // Save kdj.
            kdj = new Kdj();
            kdj.originalK = k;
            kdj.originalD = d;
            kdj.setK(k.setScale(2, RoundingMode.HALF_UP).doubleValue());
            kdj.setD(d.setScale(2, RoundingMode.HALF_UP).doubleValue());
            kdj.setJ(j.setScale(2, RoundingMode.HALF_UP).doubleValue());
            ORIGINAL_VALUE.set(specific, kdj);

            return kdj;
        };
    }

    public FactorKdj() {
        this(9);
    }

    public FactorKdj(int range) {
        this.range = range;
    }

    private final int range;

    @Override
    public void bind(Context context, Factors factors) {
        KDJ.setSupplier(factors, getSupplier(context, factors, range));
        KDJ.get(factors);
    }
}
