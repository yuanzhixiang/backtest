package com.yuanzhixiang.bt.factor.common;

import java.util.function.Supplier;

import com.yuanzhixiang.bt.domain.model.valobj.Factors;
import com.yuanzhixiang.bt.engine.Context;
import com.yuanzhixiang.bt.engine.Local;
import com.yuanzhixiang.bt.engine.Local.SuppliedLocal;
import com.yuanzhixiang.bt.factor.common.Factor;
import com.yuanzhixiang.bt.factor.common.RealPriceFactor;
import com.yuanzhixiang.bt.factor.common.RealPriceFactor.RealPrice;
import com.yuanzhixiang.bt.factor.variant.VariantFactorTangle;

/**
 * @author yuanzhixiang
 */
public class FactorTemperature implements Factor {

    // Public static methods

    public static Temperature get(Factors factors) {
        return TEMPERATURE.get(factors);
    }

    public static class Temperature {

        /**
         * Top type number
         */
        private final int top;

        /**
         * Bottom type number
         */
        private final int bottom;

        /**
         * total number
         */
        private final int total;


        public Temperature(int top, int bottom) {
            this.top = top;
            this.bottom = bottom;
            this.total = top + bottom;
        }

        public int getTop() {
            return top;
        }

        public int getBottom() {
            return bottom;
        }

        public int getTotal() {
            return total;
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // The logic of privatization

    private static final SuppliedLocal<Factors, Temperature> TEMPERATURE = new SuppliedLocal<>();

    private static final Local<Factors, Temperature> VALUE = new Local<>();

    static Supplier<Temperature> getSupplier(Context context, Factors specific, int duration) {
        return () -> {
            Temperature value = VALUE.get(specific);
            if (value != null) {
                return value;
            }

            int top = 0;
            int bottom = 0;
            for (int offset = duration * -1 - 1; offset < -1; offset++) {

                Factors factors =  VariantFactorTangle.get(specific, offset);
                Factors pFactors = VariantFactorTangle.get(specific, offset - 1);
                Factors nFactors = VariantFactorTangle.get(specific, offset + 1);

                if (factors == null || pFactors == null || nFactors == null) {
                    return null;
                }

                RealPrice realPrice = RealPriceFactor.get(factors);
                RealPrice pRealPrice = RealPriceFactor.get(pFactors);
                RealPrice nRealPrice = RealPriceFactor.get(nFactors);

                if (realPrice.getHigh() > pRealPrice.getHigh() && realPrice.getHigh() > nRealPrice.getHigh()) {
                    top++;
                    continue;
                }

                if (realPrice.getLow() < pRealPrice.getLow() && realPrice.getLow() < nRealPrice.getLow()) {
                    bottom++;
                }
            }

            Temperature temperature = new Temperature(top, bottom);
            VALUE.set(specific, temperature);
            return temperature;
        };
    }

    public FactorTemperature(int duration) {
        this.duration = duration;
    }

    /**
     * Number of k-lines included in the calculation.
     */
    private final int duration;

    @Override
    public void bind(Context context, Factors factors) {
        TEMPERATURE.setSupplier(factors, getSupplier(context, factors, duration));
    }
}
