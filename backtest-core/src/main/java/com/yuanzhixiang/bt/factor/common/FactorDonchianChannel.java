package com.yuanzhixiang.bt.factor.common;

import java.util.function.Supplier;

import com.yuanzhixiang.bt.engine.domain.Factors;
import com.yuanzhixiang.bt.service.ContextService;
import com.yuanzhixiang.bt.engine.Local.SuppliedLocal;
import com.yuanzhixiang.bt.factor.common.RealPriceFactor.RealPrice;

/**
 * @author Yuan Zhixiang
 */
public class FactorDonchianChannel implements Factor {

    public static DonchianChannel get(Factors factors) {
        return DONCHIAN_CHANNEL.get(factors);
    }

    public static class DonchianChannel {

        public DonchianChannel(double open, double close) {
            this.up = open;
            this.dn = close;
        }

        private double up;

        private double dn;

        public double getUp() {
            return up;
        }

        public double getDn() {
            return dn;
        }
    }

    private static final SuppliedLocal<Factors, DonchianChannel> DONCHIAN_CHANNEL = new SuppliedLocal<>();

    private final int n;

    public FactorDonchianChannel(int n) {
        this.n = n;
    }

    private static Supplier<DonchianChannel> getSupplier(ContextService contextService, Factors specific, int n) {
        return () -> {

            double max = 0;
            double min = 0;

            for (int offset = n * -1 + 1; offset <= 0; offset++) {
                Factors factors = contextService.getFactors(specific.getIdentity(), offset);
                if (factors == null) {
                    break;
                }

                RealPrice realPrice = RealPriceFactor.get(factors);
                max = Math.max(max, realPrice.getHigh());
                min = Math.min(min, realPrice.getLow());
            }

            if (max == 0) {
                return null;
            }

            return new DonchianChannel(max, min);
        };
    }


    @Override
    public void bind(ContextService contextService, Factors factors) {
        DONCHIAN_CHANNEL.setSupplier(factors, getSupplier(contextService, factors, n));
    }
}
