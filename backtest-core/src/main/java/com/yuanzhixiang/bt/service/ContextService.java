package com.yuanzhixiang.bt.service;

import com.yuanzhixiang.bt.engine.ExchangeAggregate;
import com.yuanzhixiang.bt.engine.ExchangeAggregate.BarIterator;
import com.yuanzhixiang.bt.engine.domain.Factors;
import com.yuanzhixiang.bt.engine.domain.Factors.Identity;
import com.yuanzhixiang.bt.engine.domain.Symbol;
import com.yuanzhixiang.bt.engine.Context;
import com.yuanzhixiang.bt.engine.Counter;
import com.yuanzhixiang.bt.engine.Local.LocalMap;
import com.yuanzhixiang.bt.engine.LocalVariable;

/**
 * @author Yuan Zhixiang
 */
public class ContextService implements Context, LocalVariable {

    public ContextService() {
    }

    private ExchangeAggregate exchange;

    private BarIterator barIterator;

    private Counter counter;

    private LocalMap localMap;

    public Factors getFactors(Symbol symbol, int offset) {
        return barIterator.getFactors(symbol, offset);
    }

    public Factors getFactors(Identity identity, int offset) {
        return barIterator.getFactors(identity, offset);
    }

    public void setExchange(ExchangeAggregate exchange) {
        this.exchange = exchange;
        barIterator = exchange.getBarIterator();
        counter = exchange.getCounter();
    }

    public ExchangeAggregate getExchange() {
        return exchange;
    }

    @Override
    public Counter getCounter() {
        return counter;
    }

    @Override
    public LocalMap getLocalMap() {
        return localMap;
    }

    @Override
    public void setLocalMap(LocalMap localMap) {
        this.localMap = localMap;
    }
}
