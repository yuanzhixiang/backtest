package com.yuanzhixiang.bt.factor.common;

import static java.math.BigDecimal.valueOf;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.yuanzhixiang.bt.engine.domain.Factors;
import com.yuanzhixiang.bt.service.ContextService;
import com.yuanzhixiang.bt.engine.ContextLocal;
import com.yuanzhixiang.bt.engine.Local;
import com.yuanzhixiang.bt.engine.Local.SuppliedLocal;

/**
 * @author Yuan Zhixiang
 */
public class FactorMaVol implements Factor<FactorMaVol> {

    public static MaVol get(Factors factors, int offset) {
        return MA_VOL.get(ContextLocal.get().getFactors(factors.getIdentity(), offset));
    }

    public static class MaVol {

        Map<Integer, Double> valueMap;

        public Double getMa(int duration) {
            return valueMap.get(duration);
        }
    }

    private static final SuppliedLocal<Factors, MaVol> MA_VOL = new SuppliedLocal<>();

    private static final Local<Factors, MaVol> ORIGIN_VALUE = new Local<>();

    static Supplier<MaVol> getSupplier(ContextService contextService, Factors specific, List<Integer> durationList) {
        return () -> {
            // The mavol is ca
            MaVol originValue = ORIGIN_VALUE.get(specific);
            if (originValue != null) {
                return originValue;
            }

            MaVol maVol = new MaVol();
            maVol.valueMap = new HashMap<>(16);
            for (Integer duration : durationList) {
                // Calculate moving average value
                long sum = 0;
                for (int offset = duration * -1 + 1; offset <= 0; offset++) {
                    Factors factor = contextService.getFactors(specific.getIdentity(), offset);
                    if (factor == null) {
                         break;
                    }
                    sum += factor.getVolume();
                }
                maVol.valueMap.put(duration, valueOf(sum).divide(valueOf(duration), 2, RoundingMode.HALF_UP).doubleValue());
            }

            ORIGIN_VALUE.set(specific, maVol);

            return maVol;
        };
    }

    public FactorMaVol(List<Integer> durationList) {
        this.durationList = List.copyOf(durationList);
    }

    private final List<Integer> durationList;

    @Override
    public void bind(ContextService contextService, Factors factors) {
        MA_VOL.setSupplier(factors, getSupplier(contextService, factors, durationList));
    }
}
