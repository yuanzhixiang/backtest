package com.yuanzhixiang.bt.factor.variant;

import java.time.LocalTime;
import java.util.List;
import java.util.function.Consumer;

import com.yuanzhixiang.bt.engine.domain.Factors;
import com.yuanzhixiang.bt.engine.domain.Symbol;
import com.yuanzhixiang.bt.engine.ContextLocal;
import com.yuanzhixiang.bt.engine.Local;

/**
 * @author Yuan Zhixiang
 */
public class PeriodFactorHourly extends AbstractPeriodFactor {

    // Public static methods

    public static Factors get(Factors factors, int offset) {
        return ContextLocal.get().getFactors(HOURLY.get(factors).getIdentity(), offset);
    }

    public static void notify(Factors factors, Consumer<Factors> periodConsumer) {
        Factors pFactors = ContextLocal.get().getFactors(factors.getSymbol(), -1);

        if (doBind(factors, pFactors)) {
            return;
        }

        Factors pHourlyFactors = get(factors, -1);
        if (pHourlyFactors != null) {
            periodConsumer.accept(pHourlyFactors);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // The logic of privatization

    private static final Local<Factors, Factors> HOURLY = new Local<>();
    private static final Local<Symbol, List<Factors>> HOURLY_DATASOURCE = new Local<>();

    @Override
    protected Local<Factors, Factors> getPeriodLocal() {
        return HOURLY;
    }

    @Override
    protected Local<Symbol, List<Factors>> getPeriodDataSourceLocal() {
        return HOURLY_DATASOURCE;
    }

    @Override
    protected boolean toMerge(Factors latest, Factors specific) {
        return doBind(latest, specific);
    }

    private static final LocalTime TIME_1 = LocalTime.of(10, 30, 0);
    private static final LocalTime TIME_2 = LocalTime.of(10, 30, 1);
    private static final LocalTime TIME_3 = LocalTime.of(11, 30, 0);
    private static final LocalTime TIME_4 = LocalTime.of(13, 0, 0);
    private static final LocalTime TIME_5 = LocalTime.of(14, 0, 0);
    private static final LocalTime TIME_6 = LocalTime.of(14, 0, 1);
    private static final LocalTime TIME_7 = LocalTime.of(9, 25);

    private static boolean doBind(Factors factors, Factors pFactors) {
        if (factors.getTradeDate().toLocalTime().isAfter(TIME_1)
            && pFactors.getTradeDate().toLocalTime().isBefore(TIME_2)) {
            return false;
        }

        if (factors.getTradeDate().toLocalTime().isAfter(TIME_3)
            && pFactors.getTradeDate().toLocalTime().isBefore(TIME_4)) {
            return false;
        }

        if (factors.getTradeDate().toLocalTime().isAfter(TIME_5)
            && pFactors.getTradeDate().toLocalTime().isBefore(TIME_6)) {
            return false;
        }

        return !factors.getTradeDate().toLocalTime().equals(TIME_7);
    }
}
