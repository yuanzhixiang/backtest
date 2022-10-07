package com.yuanzhixiang.bt.example.kit;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.yuanzhixiang.bt.engine.domain.Factors;
import com.yuanzhixiang.bt.engine.domain.Symbol;
import com.yuanzhixiang.bt.engine.Local;

import cn.hutool.core.convert.Convert;

/**
 * @author Yuan Zhixiang
 */
public class FactorsKit {

    /** 成交金额 */
    public static final Local<Factors, Double> FACTOR_AMOUNT = new Local<>();

    /** 量比 */
    public static final Local<Factors, Double> FACTOR_VOLUME_RATIO = new Local<>();
    /** 换手率 */
    public static final Local<Factors, Double> FACTOR_TURNOVER_RATE = new Local<>();
    /** 自由流通股换手率 */
    public static final Local<Factors, Double> FACTOR_FREE_TURNOVER_RATE = new Local<>();
    /** 波动幅度 */
    public static final Local<Factors, Double> FACTOR_CHANGE_PERCENTAGE = new Local<>();
    /** 涨停价 */
    public static final Local<Factors, Double> FACTOR_UP_LIMIT_PRICE = new Local<>();
    /** 跌停价 */
    public static final Local<Factors, Double> FACTOR_DOWN_LIMIT_PRICE = new Local<>();

    /** 市盈率 */
    public static final Local<Factors, Double> FACTOR_PE = new Local<>();
    /** 市盈率 TTM */
    public static final Local<Factors, Double> FACTOR_PE_TTM = new Local<>();
    /** 市净率 */
    public static final Local<Factors, Double> FACTOR_PB = new Local<>();
    /** 股息率% */
    public static final Local<Factors, Double> FACTOR_DV_RATE = new Local<>();
    /** 股息率% TTM */
    public static final Local<Factors, Double> FACTOR_DV_RATE_TTM = new Local<>();

    /** 市销率 */
    public static final Local<Factors, Double> FACTOR_PS = new Local<>();
    /** 市销率 TTM */
    public static final Local<Factors, Double> FACTOR_PS_TTM = new Local<>();

    /** 总市值，单位 元 */
    public static final Local<Factors, Double> FACTOR_TOTAL_CAPITAL = new Local<>();
    /** 流通市值，单位 元 */
    public static final Local<Factors, Double> FACTOR_CIRCULATION_CAPITAL = new Local<>();

    /** 总股本，单位 股 */
    public static final Local<Factors, Double> FACTOR_TOTAL_EQUITY = new Local<>();
    /** 流通股本，单位 股 */
    public static final Local<Factors, Double> FACTOR_TRADABLE_EQUITY = new Local<>();
    /** 可自由交易的流通股本，单位 股 */
    public static final Local<Factors, Double> FACTOR_FREE_TRADABLE_EQUITY = new Local<>();

    private static final Map<String, Local<Factors, Double>> LOCAL_MAPPING = new HashMap<>(16);

    static {
        LOCAL_MAPPING.put("amount", FACTOR_AMOUNT);
        LOCAL_MAPPING.put("volumeRatio", FACTOR_VOLUME_RATIO);
        LOCAL_MAPPING.put("turnoverRate", FACTOR_TURNOVER_RATE);
        LOCAL_MAPPING.put("freeTurnoverRate", FACTOR_FREE_TURNOVER_RATE);
        LOCAL_MAPPING.put("changePercentage", FACTOR_CHANGE_PERCENTAGE);
        LOCAL_MAPPING.put("upLimitPrice", FACTOR_UP_LIMIT_PRICE);
        LOCAL_MAPPING.put("downLimitPrice", FACTOR_DOWN_LIMIT_PRICE);
        LOCAL_MAPPING.put("pe", FACTOR_PE);
        LOCAL_MAPPING.put("peTtm", FACTOR_PE_TTM);
        LOCAL_MAPPING.put("pb", FACTOR_PB);
        LOCAL_MAPPING.put("dvRate", FACTOR_DV_RATE);
        LOCAL_MAPPING.put("dvRateTtm", FACTOR_DV_RATE_TTM);
        LOCAL_MAPPING.put("ps", FACTOR_PS);
        LOCAL_MAPPING.put("psTtm", FACTOR_PS_TTM);
        LOCAL_MAPPING.put("totalCapital", FACTOR_TOTAL_CAPITAL);
        LOCAL_MAPPING.put("circulationCapital", FACTOR_CIRCULATION_CAPITAL);
        LOCAL_MAPPING.put("totalEquity", FACTOR_TOTAL_EQUITY);
        LOCAL_MAPPING.put("tradableEquity", FACTOR_TRADABLE_EQUITY);
        LOCAL_MAPPING.put("freeTradableEquity", FACTOR_FREE_TRADABLE_EQUITY);
    }

    public static Factors mapToFactors(Symbol symbol, Map<String, Object> valueMap) {
        LocalDateTime tradingDate = Convert.convert(LocalDateTime.class, valueMap.get("tradingDate"));
        Double open = Convert.convert(Double.class, valueMap.get("open"));
        Double close = Convert.convert(Double.class, valueMap.get("close"));
        Double high = Convert.convert(Double.class, valueMap.get("high"));
        Double low = Convert.convert(Double.class, valueMap.get("low"));
        Long volume = (long) (Convert.convert(Double.class, valueMap.get("volume")) * 100);
        BigDecimal adjustment = Convert.convert(BigDecimal.class, valueMap.get("priceAdjustmentFactor"));
        Factors factors = new Factors(symbol, tradingDate, open, close, high, low, volume, adjustment);

        for (Entry<String, Local<Factors, Double>> entry : LOCAL_MAPPING.entrySet()) {
            entry.getValue().set(factors, Convert.convert(Double.class, valueMap.get(entry.getKey())));
        }

        return factors;
    }

}
