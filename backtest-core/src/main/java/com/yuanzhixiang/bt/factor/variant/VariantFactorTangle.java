package com.yuanzhixiang.bt.factor.variant;

import java.util.ArrayList;
import java.util.List;

import com.yuanzhixiang.bt.domain.model.valobj.Factors;
import com.yuanzhixiang.bt.domain.model.valobj.Symbol;
import com.yuanzhixiang.bt.engine.Context;
import com.yuanzhixiang.bt.engine.ContextLocal;
import com.yuanzhixiang.bt.engine.Local;
import com.yuanzhixiang.bt.exception.BackTestException;
import com.yuanzhixiang.bt.factor.common.RealPriceFactor;

/**
 * @author yuanzhixiang
 */
public class VariantFactorTangle implements VariantFactor {

    // Public static methods

    public static Factors get(Factors factors, int offset) {
        return ContextLocal.get().getFactors(TANGLE.get(factors).getIdentity(), offset);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // The logic of privatization

    private static final Local<Factors, Factors> TANGLE = new Local<>();
    private static final Local<Symbol, List<Factors>> DATASOURCE = new Local<>();

    @Override
    public Factors bind(Context context, Factors factors) {
        List<Factors> factorsList = DATASOURCE.get(factors.getSymbol());

        if (factorsList == null) {
            factorsList = new ArrayList<>();
            DATASOURCE.set(factors.getSymbol(), factorsList);
        }

        // If factorsList size is zero, then initialize the first node.
        if (factorsList.size() == 0) {
            Factors newFactors = new Factors(DATASOURCE, factors.getSymbol(), factors.getTradeDate(), factors.getOpen(),
                factors.getClose(), factors.getHigh(), factors.getLow(), factors.getVolume());
            factorsList.add(newFactors);
            TANGLE.set(factors, newFactors);
            return newFactors;
        }

        // Offset -1
        Factors pFactor = factorsList.get(factorsList.size() - 1);

        Factors newFactors;
        // Determine whether there is a containment relationship
        boolean contain1 = RealPriceFactor.ge(factors.getSymbol(), pFactor.getHigh(), factors.getHigh())
            && RealPriceFactor.le(factors.getSymbol(), pFactor.getLow(), factors.getLow());
        boolean contain2 = RealPriceFactor.le(factors.getSymbol(), pFactor.getHigh(), factors.getHigh())
            && RealPriceFactor.ge(factors.getSymbol(), pFactor.getLow(), factors.getLow());
        if (contain1 || contain2) {
            // If the data of bar is less than 2, then the default is an upward trend.
            if (factorsList.size() < 2) {
                newFactors = new Factors(DATASOURCE, factors.getSymbol(), factors.getTradeDate(), pFactor.getOpen(),
                    factors.getClose(),
                    RealPriceFactor.max(factors.getSymbol(), factors.getHigh(), pFactor.getHigh()),
                    RealPriceFactor.max(factors.getSymbol(), factors.getLow(), pFactor.getLow()),
                    factors.getVolume());
            }
            // Judge the trend based on the data of the previous two days.
            else {
                // If there is a containment relationship between the two nearest bars, then the two bars are processed.
                Factors ppFactor = factorsList.get(factorsList.size() - 1 - 1);

                // Downtrend
                if (RealPriceFactor.le(factors.getSymbol(), pFactor.getHigh(), ppFactor.getHigh())
                    && RealPriceFactor.le(factors.getSymbol(), pFactor.getLow(), ppFactor.getLow())) {
                    newFactors = new Factors(DATASOURCE, factors.getSymbol(), factors.getTradeDate(), pFactor.getOpen(),
                        factors.getClose(),
                        RealPriceFactor.min(factors.getSymbol(), factors.getHigh(), pFactor.getHigh()),
                        RealPriceFactor.min(factors.getSymbol(), factors.getLow(), pFactor.getLow()),
                        factors.getVolume());
                }
                // Upward trend
                else if (RealPriceFactor.ge(factors.getSymbol(), pFactor.getHigh(), ppFactor.getHigh())
                    && RealPriceFactor.ge(factors.getSymbol(), pFactor.getLow(), ppFactor.getLow())) {
                    newFactors = new Factors(DATASOURCE, factors.getSymbol(), factors.getTradeDate(), pFactor.getOpen(),
                        factors.getClose(),
                        RealPriceFactor.max(factors.getSymbol(), factors.getHigh(), pFactor.getHigh()),
                        RealPriceFactor.max(factors.getSymbol(), factors.getLow(), pFactor.getLow()),
                        factors.getVolume());
                } else {
                    throw new BackTestException("It is impossible to go here, if it appears, "
                        + "it means that there is an error in the code logic.");
                }
            }

            // Delete the most recent non-trend line.
            factorsList.remove(factorsList.size() - 1);
        } else {
            newFactors = new Factors(DATASOURCE, factors.getSymbol(), factors.getTradeDate(), factors.getOpen(),
                factors.getClose(), factors.getHigh(), factors.getLow(), factors.getVolume());
        }

        // Store data
        factorsList.add(newFactors);
        TANGLE.set(factors, newFactors);

        if (factorsList.size() > 30) {
            factorsList.remove(0);
        }
        return newFactors;
    }
}
