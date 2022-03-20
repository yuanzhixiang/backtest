package com.yuanzhixiang.bt.factor.common;

import static com.yuanzhixiang.bt.kit.BigDecimalConstants._0;
import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.yuanzhixiang.bt.domain.model.valobj.Factors;
import com.yuanzhixiang.bt.domain.model.valobj.Symbol;
import com.yuanzhixiang.bt.domain.model.valobj.Price;
import com.yuanzhixiang.bt.engine.ContextLocal;
import com.yuanzhixiang.bt.factor.common.LatestAdjustmentFactor;

/**
 * @author yuanzhixiang
 */
public class RealPriceFactor {

    // Public static methods

    public static RealPrice get(Factors factors) {
        return get(factors, 0);
    }

    public static RealPrice get(Factors factors, int offset) {
        BigDecimal latestAdjustment = LatestAdjustmentFactor.get(factors.getSymbol());
        Factors offsetFactors = ContextLocal.get().getFactors(factors.getIdentity(), offset);
        double open = getRealPrice(offsetFactors.getOpen(), latestAdjustment);
        double close = getRealPrice(offsetFactors.getClose(), latestAdjustment);
        double high = getRealPrice(offsetFactors.getHigh(), latestAdjustment);
        double low = getRealPrice(offsetFactors.getLow(), latestAdjustment);
        return new RealPrice(open, close, high, low);
    }

    public static double get(Symbol symbol, Price price) {
        return getRealPrice(price, LatestAdjustmentFactor.get(symbol));
    }

    public static BigDecimal getForCompute(Symbol symbol, Price price) {
        return getRealPriceForCompute(price, LatestAdjustmentFactor.get(symbol));
    }

    public static Price max(Symbol symbol, Price p1, Price p2) {
        double realP1 = get(symbol, p1);
        return realP1 == Math.max(realP1, get(symbol, p2)) ? p1 : p2;
    }

    public static Price min(Symbol symbol, Price p1, Price p2) {
        double realP1 = get(symbol, p1);
        return realP1 == Math.min(realP1, get(symbol, p2)) ? p1 : p2;
    }

    public static boolean gt(Symbol symbol, Price p1, Price p2) {
        return get(symbol, p1) > get(symbol, p2);
    }

    @Deprecated
    public static boolean ge(Symbol symbol, Price p1, Price p2) {
        return get(symbol, p1) >= get(symbol, p2);
    }

    public static boolean lt(Symbol symbol, Price p1, Price p2) {
        return get(symbol, p1) < get(symbol, p2);
    }

    @Deprecated
    public static boolean le(Symbol symbol, Price p1, Price p2) {
        return get(symbol, p1) <= get(symbol, p2);
    }

    public static BigDecimal subForCompute(Symbol symbol, Price p1, Price p2) {
        return getForCompute(symbol, p1).subtract(getForCompute(symbol, p2));
    }

    public static BigDecimal addForCompute(Symbol symbol, Price p1, Price p2) {
        return getForCompute(symbol, p1).add(getForCompute(symbol, p2));
    }

    public static BigDecimal addForCompute(Symbol symbol, Price ...pArr) {
        if (pArr == null || pArr.length == 0) {
            return _0;
        }

        if (pArr.length == 1) {
            return getForCompute(symbol, pArr[0]);
        }

        BigDecimal sum = _0;
        for (Price price : pArr) {
            sum = sum.add(getForCompute(symbol, price));
        }
        return sum;
    }

    public static class RealPrice {

        private final double open;

        private final double close;

        private final double high;

        private final double low;

        public RealPrice(double open, double close, double high, double low) {
            this.open = open;
            this.close = close;
            this.high = high;
            this.low = low;
        }

        public double getOpen() {
            return open;
        }

        public double getClose() {
            return close;
        }

        public double getHigh() {
            return high;
        }

        public double getLow() {
            return low;
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    // The logic of privatization

    private static double getRealPrice(Price price, BigDecimal latestAdjustment) {
        return getRealPriceForCompute(price, latestAdjustment).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private static BigDecimal getRealPriceForCompute(Price price, BigDecimal latestAdjustment) {
        BigDecimal adjustment = price.getAdjustment();
        BigDecimal realAdjustment = adjustment.divide(latestAdjustment, 4, RoundingMode.HALF_UP);
        return valueOf(price.getPrice()).multiply(realAdjustment);
    }
}
