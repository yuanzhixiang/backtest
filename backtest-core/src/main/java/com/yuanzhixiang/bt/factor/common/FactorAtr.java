package com.yuanzhixiang.bt.factor.common;

import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Supplier;

import com.yuanzhixiang.bt.service.ContextService;
import com.yuanzhixiang.bt.engine.ContextLocal;
import com.yuanzhixiang.bt.engine.Local.SuppliedLocal;
import com.yuanzhixiang.bt.engine.domain.Factors;

/**
 * @author Yuan Zhixiang
 */
public class FactorAtr implements Factor<FactorAtr> {

    public static Double get(Factors factors, int offset) {
        return ATR.get(ContextLocal.get().getFactors(factors.getIdentity(), offset));
    }

    private static final SuppliedLocal<Factors, Double> ATR = new SuppliedLocal<>();

    private static Supplier<Double> getSupplier(ContextService contextService, Factors specific, BigDecimal range) {
        return () -> {
            BigDecimal sum = valueOf(0);

            for (int offset = range.intValue() * -1 + 1; offset <= 0; offset++) {
                Factors factors = contextService.getFactors(specific.getIdentity(), offset);
                Factors previousFactors = contextService.getFactors(specific.getIdentity(), offset - 1);

                if (factors == null || previousFactors == null) {
                    return sum.divide(range.add(valueOf(offset)), 4, RoundingMode.HALF_UP).doubleValue();
                }

                BigDecimal high = valueOf(RealPriceFactor.get(factors.getSymbol(), factors.getHigh()));
                BigDecimal low = valueOf(RealPriceFactor.get(factors.getSymbol(), factors.getLow()));
                BigDecimal previousClose = valueOf(RealPriceFactor.get(previousFactors.getSymbol(), previousFactors.getClose()));

                BigDecimal value1 = high.subtract(low);
                BigDecimal value2 = high.subtract(previousClose).abs();
                BigDecimal value3 = low.subtract(previousClose).abs();

                BigDecimal tr = value1.max(value2).max(value3);
                sum = sum.add(tr);
            }

            return sum.divide(range, 4, RoundingMode.HALF_UP).doubleValue();
        };
    }

    private final BigDecimal range;

    public FactorAtr(int range) {
        this.range = valueOf(range);
    }

    @Override
    public void bind(ContextService contextService, Factors factors) {
        ATR.setSupplier(factors, getSupplier(contextService, factors, range));
    }
}
