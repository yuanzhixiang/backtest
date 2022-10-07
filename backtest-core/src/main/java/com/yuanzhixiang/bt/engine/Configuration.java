package com.yuanzhixiang.bt.engine;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yuanzhixiang.bt.engine.domain.Symbol;
import com.yuanzhixiang.bt.engine.market.Market;
import com.yuanzhixiang.bt.factor.common.Factor;
import com.yuanzhixiang.bt.factor.variant.VariantFactor;

/**
 * @author Yuan Zhixiang
 */
public class Configuration {

    private List<Symbol> symbolList;

    private Symbol benchmark;

    private Counter counter;

    private Market market;

    public List<Symbol> getSymbolList() {
        return symbolList;
    }

    public void setSymbolList(List<Symbol> symbolList) {
        this.symbolList = symbolList;
    }

    public Symbol getBenchmark() {
        return benchmark;
    }

    public void setBenchmark(Symbol benchmark) {
        this.benchmark = benchmark;
    }

    public Counter getCounter() {
        return counter;
    }

    public void setCounter(Counter counter) {
        this.counter = counter;
    }

    public Market getDataSource() {
        return market;
    }

    public void setDataSource(Market market) {
        this.market = market;
    }

    private Map<String, Factor<?>> factorMaps = new HashMap<>();
    private Map<String, VariantFactor> variantFactorMap = new HashMap<>();
    private Map<String, LifeCycle> lifeCycleMap = new HashMap<>();

    public Collection<Factor<?>> getAllFactor() {
        return factorMaps.values();
    }

    public Collection<VariantFactor> getVariantFactorMaps() {
        return variantFactorMap.values();
    }

    public Collection<LifeCycle> getLifeCycleList() {
        return lifeCycleMap.values();
    }

    public <F extends Factor<F>> void registerFactor(F factor) {
        if (factor == null) {
            throw new IllegalArgumentException("Factor is null");
        }
        F old = (F) factorMaps.remove(factor.getClass().toString());
        factor.register(this, old);
        factorMaps.put(factor.getClass().toString(), factor);
    }

    public void registerPeriodFactor(VariantFactor ...variantFactorArray) {
        putValue(variantFactorMap, variantFactorArray);
    }

    public void registerLifeCycle(LifeCycle ...lifeCycleArray) {
        putValue(lifeCycleMap, lifeCycleArray);
    }

    private void putValue(Map map, Object ...objArr) {
        for (Object obj : objArr) {
            Object old = map.get(obj.toString());


            map.put(obj.toString(), obj);
        }
    }

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setTimeRange(LocalDateTime startDate, LocalDateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
