package com.yuanzhixiang.bt.factor.variant;

import java.util.List;

import com.yuanzhixiang.bt.engine.domain.Factors;
import com.yuanzhixiang.bt.engine.domain.Symbol;
import com.yuanzhixiang.bt.engine.ContextLocal;
import com.yuanzhixiang.bt.engine.Local;

/**
 * @author Yuan Zhixiang
 */
public class PeriodFactorDaily extends AbstractPeriodFactor {

    public static Factors get(Factors factors, int offset) {
        return ContextLocal.get().getFactors(DAILY.get(factors).getIdentity(), offset);
    }

    private static final Local<Factors, Factors> DAILY = new Local<>();
    private static final Local<Symbol, List<Factors>> DAILY_DATASOURCE = new Local<>();

    @Override
    protected Local<Factors, Factors> getPeriodLocal() {
        return DAILY;
    }

    @Override
    protected Local<Symbol, List<Factors>> getPeriodDataSourceLocal() {
        return DAILY_DATASOURCE;
    }

    @Override
    protected boolean toMerge(Factors latest, Factors specific) {
        return doBind(latest, specific);
    }

    private static boolean doBind(Factors latest, Factors specific) {
        return specific.getTradeDate().getDayOfYear() == latest.getTradeDate().getDayOfYear();
    }

    public static boolean change(Factors factors) {
        Factors pFactors = ContextLocal.get().getFactors(factors.getIdentity(), -1);
        if (pFactors == null) {
            return false;
        }
        return !doBind(factors, pFactors);
    }
}
