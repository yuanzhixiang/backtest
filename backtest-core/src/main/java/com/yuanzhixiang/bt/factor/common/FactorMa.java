package com.yuanzhixiang.bt.factor.common;

import static com.yuanzhixiang.bt.kit.BigDecimalConstants._0;
import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.yuanzhixiang.bt.engine.domain.Factors;
import com.yuanzhixiang.bt.engine.Configuration;
import com.yuanzhixiang.bt.service.ContextService;
import com.yuanzhixiang.bt.engine.ContextLocal;
import com.yuanzhixiang.bt.engine.Local.SuppliedLocal;

/**
 * @author Yuan Zhixiang
 */
public class FactorMa implements Factor<FactorMa> {

    // Public static methods

    /**
     * Get the average value.
     *
     * @param factors period
     * @param offset the offset from the current bar position, which is less than or equal to 0
     * @return Average value
     */
    public static Ma get(Factors factors, int offset) {
        return MA.get(ContextLocal.get().getFactors(factors.getIdentity(), offset));
    }

    /**
     * Determine if a golden cross in the averages has occurred.
     *
     * @param factors period
     * @param p1      short-term averages
     * @param p2      long-term averages
     * @return Golden cross appears
     */
    public static boolean golden(Factors factors, int p1, int p2) {

        Ma pMa = get(factors, -1);
        Ma ppMa = get(factors, -2);

        if (checkNotNull(pMa, p1, p2) || checkNotNull(ppMa, p1, p2)) {
            return false;
        }

        return pMa.getMa(p1) > pMa.getMa(p2) && ppMa.getMa(p1) <= ppMa.getMa(p2);
    }

    /**
     * Determine if a dead cross in the averages has occurred.
     *
     * @param factors period
     * @param p1      short-term averages
     * @param p2      long-term averages
     * @return Dead cross appears
     */
    public static boolean dead(Factors factors, int p1, int p2) {

        Ma pMa = get(factors, -1);
        Ma ppMa = get(factors, -2);

        if (checkNotNull(pMa, p1, p2) || checkNotNull(ppMa, p1, p2)) {
            return false;
        }

        return ppMa.getMa(p1) >= ppMa.getMa(p2) && pMa.getMa(p1) < pMa.getMa(p2);
    }

    private static boolean checkNotNull(Ma ma, int p1, int p2) {
        return ma == null || ma.getMa(p1) == null || ma.getMa(p2) == null;
    }

    public static class Ma {

        Map<Integer, Double> valueMap;

        public Double getMa(int duration) {
            return valueMap.get(duration);
        }
    }


    // -----------------------------------------------------------------------------------------------------------------
    // The logic of privatization

    private static final SuppliedLocal<Factors, Ma> MA = new SuppliedLocal<>();

    static Supplier<Ma> getSupplier(ContextService contextService, Factors specific, Set<Integer> durationList) {
        return () -> {
            Ma ma = new Ma();
            ma.valueMap = new HashMap<>(16);
            for (Integer duration : durationList) {
                // Calculate moving average value
                BigDecimal sum = _0;
                for (int offset = duration * -1 + 1; offset <= 0; offset++) {
                    Factors factor = contextService.getFactors(specific.getIdentity(), offset);
                    if (factor == null) {
                         break;
                    }
                    sum = sum.add(valueOf(RealPriceFactor.get(factor.getSymbol(), factor.getClose())));
                }
                ma.valueMap.put(duration, sum.divide(valueOf(duration), 2, RoundingMode.HALF_UP).doubleValue());
            }

            return ma;
        };
    }

    public FactorMa(Integer ...cycleArr) {
        if (cycleArr == null || cycleArr.length == 0) {
            throw new IllegalArgumentException("The cycle array is null or empty.");
        }
        cycleSet = new HashSet<>();
        cycleSet.addAll(Arrays.asList(cycleArr));
    }

    private final Set<Integer> cycleSet;

    public Set<Integer> getCycleSet() {
        return Set.copyOf(cycleSet);
    }

    @Override
    public void register(Configuration configuration, FactorMa old) {
        if (old != null) {
            cycleSet.addAll(old.cycleSet);
        }
    }

    @Override
    public void bind(ContextService contextService, Factors factors) {
        MA.setSupplier(factors, getSupplier(contextService, factors, cycleSet));
    }
}
