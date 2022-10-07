package com.yuanzhixiang.bt.factor.variant;

import java.util.List;

import com.yuanzhixiang.bt.engine.domain.Factors;
import com.yuanzhixiang.bt.engine.domain.Symbol;
import com.yuanzhixiang.bt.engine.ContextLocal;
import com.yuanzhixiang.bt.engine.Local;

/**
 * @author Yuan Zhixiang
 */
public class PeriodFactorMonthly extends AbstractPeriodFactor {

    public static Factors get(Factors factors) {
        return MONTHLY.get(factors);
    }

    private static final Local<Factors, Factors> MONTHLY = new Local<>();
    private static final Local<Symbol, List<Factors>> MONTHLY_DATASOURCE = new Local<>();

    @Override
    protected Local<Factors, Factors> getPeriodLocal() {
        return MONTHLY;
    }

    @Override
    protected Local<Symbol, List<Factors>> getPeriodDataSourceLocal() {
        return MONTHLY_DATASOURCE;
    }

    @Override
    protected boolean toMerge(Factors latest, Factors specific) {
        return doBind(latest, specific);
    }

    private static boolean doBind(Factors latest, Factors specific) {
        return specific.getTradeDate().getMonthValue() == latest.getTradeDate().getMonthValue();
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
