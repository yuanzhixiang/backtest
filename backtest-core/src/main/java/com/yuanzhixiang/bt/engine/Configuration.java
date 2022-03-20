package com.yuanzhixiang.bt.engine;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yuanzhixiang.bt.domain.model.valobj.Symbol;
import com.yuanzhixiang.bt.domain.repository.DataSource;
import com.yuanzhixiang.bt.factor.common.Factor;
import com.yuanzhixiang.bt.factor.variant.VariantFactor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author yuanzhixiang
 */
public class Configuration {

    @Getter
    @Setter
    private List<Symbol> symbolList;

    @Getter
    @Setter
    private Symbol benchmark;

    @Getter
    @Setter
    private double commissionRate;

    @Getter
    @Setter
    private int minCommission = 5;

    @Getter
    @Setter
    private double accountBalance;

    private DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private boolean todayAddOneTrade = true;

    public boolean isTodayAddOneTrade() {
        return todayAddOneTrade;
    }

    public void setTodayAddOneTrade(boolean todayAddOneTrade) {
        this.todayAddOneTrade = todayAddOneTrade;
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

    public Configuration deepClone() {
        Configuration configuration = new Configuration();
        configuration.symbolList = new ArrayList<>(symbolList);
        configuration.benchmark = benchmark;
        configuration.dataSource = dataSource;
        configuration.commissionRate = commissionRate;
        configuration.minCommission = minCommission;
        configuration.accountBalance = accountBalance;

        configuration.factorMaps = factorMaps;
        configuration.variantFactorMap = variantFactorMap;
        configuration.lifeCycleMap = lifeCycleMap;

        configuration.startDate = startDate;
        configuration.endDate = endDate;
        return configuration;
    }
}
