package com.yuanzhixiang.bt.factor.common;

import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Supplier;

import com.yuanzhixiang.bt.service.ContextService;
import com.yuanzhixiang.bt.engine.Local.SuppliedLocal;
import com.yuanzhixiang.bt.engine.domain.Factors;

/**
 * @author Yuan Zhixiang
 */
public class FactorBoll implements Factor<FactorBoll> {

    public static Boll get(Factors factors) {
        return BOLL.get(factors);
    }

    public static class Boll {

        private final double mb;

        private final double up;

        private final double dn;

        public Boll(double mb, double up, double dn) {
            this.mb = mb;
            this.up = up;
            this.dn = dn;
        }

        public Double getMb() {
            return mb;
        }

        public Double getUp() {
            return up;
        }

        public Double getDn() {
            return dn;
        }
    }

    private static final SuppliedLocal<Factors, Boll> BOLL = new SuppliedLocal<>();

    private static Supplier<Boll> getSupplier(ContextService contextService, Factors factors, BigDecimal duration, BigDecimal standardRatio) {
        return () -> {
            BigDecimal ma = getMa(contextService, factors, duration);
            if (ma == null) {
                return null;
            }
            BigDecimal md = getMd(contextService, factors, duration, ma);
            double mb = ma.setScale(2, RoundingMode.HALF_UP).doubleValue();
            double up = ma.add(standardRatio.multiply(md)).setScale(2, RoundingMode.HALF_UP).doubleValue();
            double dn = ma.subtract(standardRatio.multiply(md)).setScale(2, RoundingMode.HALF_UP).doubleValue();

            return new Boll(mb, up, dn);
        };
    }

    private static BigDecimal getMa(ContextService contextService, Factors specific, BigDecimal duration) {
        BigDecimal maSum = valueOf(0);
        for (int i = duration.intValue() * -1 + 1; i <= 0; i++) {
            Factors factors = contextService.getFactors(specific.getIdentity(), i);
            if (factors == null) {
                return null;
            }
            maSum = valueOf(RealPriceFactor.get(factors.getSymbol(), factors.getClose())).add(maSum);
        }
        return maSum.divide(duration, 2, RoundingMode.HALF_UP);
    }

    private static BigDecimal getMd(ContextService contextService, Factors specific, BigDecimal duration, BigDecimal ma) {
        BigDecimal mdSum = valueOf(0);
        for (int i = duration.intValue() * -1 + 1; i <= 0; i++) {
            Factors factors = contextService.getFactors(specific.getIdentity(), i);
            BigDecimal difference = valueOf(RealPriceFactor.get(factors.getSymbol(), factors.getClose())).subtract(ma);
            mdSum = difference.multiply(difference).add(mdSum);
        }
        return valueOf(Math.sqrt(mdSum.divide(duration, 4, RoundingMode.HALF_UP).doubleValue()));
    }

    private final BigDecimal standardRatio;

    private final BigDecimal duration;

    public FactorBoll() {
        this(20, 2);
    }

    public FactorBoll(int duration, int standardRatio) {
        this.standardRatio = valueOf(standardRatio);
        this.duration = valueOf(duration);
    }

    @Override
    public void bind(ContextService contextService, Factors factors) {
        BOLL.setSupplier(factors, getSupplier(contextService, factors, duration, standardRatio));
    }
}
