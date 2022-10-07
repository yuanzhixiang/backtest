package com.yuanzhixiang.bt.factor.variant;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yuanzhixiang.bt.engine.domain.Factors;
import com.yuanzhixiang.bt.engine.domain.Symbol;
import com.yuanzhixiang.bt.engine.ContextLocal;
import com.yuanzhixiang.bt.engine.Local;

/**
 * @author Yuan Zhixiang
 */
public class PeriodFactorWeekly extends AbstractPeriodFactor {

    public static Factors get(Factors factors) {
        return WEEKLY.get(factors);
    }

    private static final Local<Factors, Factors> WEEKLY = new Local<>();
    private static final Local<Symbol, List<Factors>> WEEKLY_DATASOURCE = new Local<>();

    @Override
    protected Local<Factors, Factors> getPeriodLocal() {
        return WEEKLY;
    }

    @Override
    protected Local<Symbol, List<Factors>> getPeriodDataSourceLocal() {
        return WEEKLY_DATASOURCE;
    }

    private static final Map<DayOfWeek, Integer> DURATION_MAP = new HashMap<>();

    static {
        DURATION_MAP.put(DayOfWeek.MONDAY, 0);
        DURATION_MAP.put(DayOfWeek.TUESDAY, 1);
        DURATION_MAP.put(DayOfWeek.WEDNESDAY, 2);
        DURATION_MAP.put(DayOfWeek.THURSDAY, 3);
        DURATION_MAP.put(DayOfWeek.FRIDAY, 4);
        DURATION_MAP.put(DayOfWeek.SATURDAY, 5);
        DURATION_MAP.put(DayOfWeek.SUNDAY, 6);
    }

    @Override
    protected boolean toMerge(Factors latest, Factors specific) {
       return doBind(latest, specific);
    }

    private static boolean doBind(Factors latest, Factors specific) {
        LocalDate tradeDate = latest.getTradeDate().toLocalDate();
        LocalDate specificDate = specific.getTradeDate().toLocalDate();
        return tradeDate.getDayOfYear() - specificDate.getDayOfYear() <= DURATION_MAP.get(tradeDate.getDayOfWeek());
    }

    public static boolean change(Factors factors) {
        Factors periodFactor = get(factors);
        if (periodFactor == null) {
            return false;
        }
        Factors previousFactors = ContextLocal.get().getFactors(periodFactor.getIdentity(), -1);
        if (previousFactors == null) {
            return false;
        }
        return !doBind(factors, previousFactors);
    }
}
