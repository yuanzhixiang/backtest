package com.yuanzhixiang.bt.factor.variant;

import java.util.ArrayList;
import java.util.List;

import com.yuanzhixiang.bt.domain.model.valobj.Factors;
import com.yuanzhixiang.bt.domain.model.valobj.Price;
import com.yuanzhixiang.bt.domain.model.valobj.Symbol;
import com.yuanzhixiang.bt.engine.Context;
import com.yuanzhixiang.bt.engine.Local;
import com.yuanzhixiang.bt.factor.common.RealPriceFactor;

/**
 * @author yuanzhixiang
 */
public abstract class AbstractPeriodFactor implements VariantFactor {

    @Override
    public final Factors bind(Context context, Factors factors) {
        Price open = factors.getOpen();
        Price close = factors.getClose();
        Price high = factors.getHigh();
        Price low = factors.getLow();
        long volume = factors.getVolume();

        Factors specific = context.getFactors(factors.getIdentity(), -1);
        if (specific != null && toMerge(factors, specific)) {
            Factors periodFactors = getPeriodLocal().get(specific);
            open = periodFactors.getOpen();
            high = RealPriceFactor.max(specific.getSymbol(), high, periodFactors.getHigh());
            low = RealPriceFactor.min(specific.getSymbol(), low, periodFactors.getLow());
            volume += periodFactors.getVolume();
        }

        Factors periodFactor = new Factors(getPeriodDataSourceLocal(), factors.getSymbol(), factors.getTradeDate(), open, close, high, low, volume);
        getPeriodLocal().set(factors, periodFactor);

        // Get period factors list
        List<Factors> factorsList = getPeriodDataSourceLocal().get(factors.getSymbol());
        if (factorsList == null) {
            factorsList = new ArrayList<>();
            getPeriodDataSourceLocal().set(factors.getSymbol(), factorsList);
        }

        // Update period factors
        if (factorsList.size() != 0 && toMerge(factors, factorsList.get(factorsList.size() - 1))) {
            factorsList.remove(factorsList.size() - 1);
        }
        factorsList.add(periodFactor);

        // Remove old data
        if (factorsList.size() > 30) {
            factorsList.remove(0);
        }
        return periodFactor;
    }

    /**
     * The local is used to store data in base factors
     *
     * @return Local
     */
    protected abstract Local<Factors, Factors> getPeriodLocal();

    /**
     * The local is used to store data in symbol
     *
     * @return Local
     */
    protected abstract Local<Symbol, List<Factors>> getPeriodDataSourceLocal();

    /**
     * Cross-period means that there are holidays in the middle, skip directly.
     *
     * @param latest   Latest factors
     * @param specific Specific factors
     * @return whether need to bind
     */
    protected abstract boolean toMerge(Factors latest, Factors specific);
}
