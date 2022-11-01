package com.yuanzhixiang.bt.factor.common;

import com.yuanzhixiang.bt.engine.domain.Factors;
import com.yuanzhixiang.bt.engine.domain.Price;
import com.yuanzhixiang.bt.engine.domain.Symbol;
import com.yuanzhixiang.bt.engine.ContextLocal;
import com.yuanzhixiang.bt.kit.LambdaElement;

import java.math.BigDecimal;

/**
 * @author Yuan Zhixiang
 */
class MergerFactor {

    public static Factors get(Factors factors, int offset, int cycle) {
        factors = ContextLocal.get().getFactors(factors.getIdentity(), offset);

        LambdaElement<Price> openElement = new LambdaElement<>(null);
        LambdaElement<Price> max = new LambdaElement<>(factors.getHigh());
        LambdaElement<Price> min = new LambdaElement<>(factors.getLow());
        LambdaElement<BigDecimal> volumeElement = new LambdaElement<>(BigDecimal.ZERO);
        Symbol symbol = factors.getSymbol();
        Factors.foreach(cycle, factors, offsetFactors -> {
            if (openElement.getE() == null) {
                openElement.setE(offsetFactors.getOpen());
            }

            Price high = offsetFactors.getHigh();
            if (RealPriceFactor.lt(symbol, max.getE(), high)) {
                max.setE(high);
            }

            Price low = offsetFactors.getLow();
            if (RealPriceFactor.gt(symbol, min.getE(), low)) {
                min.setE(low);
            }

            volumeElement.setE(volumeElement.getE().add(offsetFactors.getVolume()));
        });

        Price open = openElement.getE();
        Price close = factors.getClose();
        Price high = max.getE();
        Price low = min.getE();
        BigDecimal volume = volumeElement.getE();

        return new Factors(null, factors.getSymbol(), factors.getTradeDate(), open, close, high, low, volume);
    }

}
