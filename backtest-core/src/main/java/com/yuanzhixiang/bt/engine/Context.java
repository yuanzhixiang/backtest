package com.yuanzhixiang.bt.engine;

import java.time.LocalDateTime;
import java.util.List;

import com.yuanzhixiang.bt.domain.model.aggregate.SecuritiesExchangeAggregate;
import com.yuanzhixiang.bt.domain.model.aggregate.SecuritiesExchangeAggregate.BarIterator;
import com.yuanzhixiang.bt.domain.model.entity.AccountEntity;
import com.yuanzhixiang.bt.domain.model.valobj.Factors;
import com.yuanzhixiang.bt.domain.model.valobj.Factors.Identity;
import com.yuanzhixiang.bt.domain.model.valobj.Symbol;
import com.yuanzhixiang.bt.domain.model.valobj.position.Position;
import com.yuanzhixiang.bt.engine.Local.LocalMap;

/**
 * @author yuanzhixiang
 */
public class Context implements LocalVariable {

    public Context(Configuration configuration) {
        this.configuration = configuration;
    }

    private final Configuration configuration;

    private SecuritiesExchangeAggregate securitiesExchange;

    private BarIterator barIterator;

    private AccountEntity account;

    private LocalMap localMap;

    // Configuration

    public Configuration getConfiguration() {
        return configuration;
    }

    public List<Symbol> getTradeSymbolList() {
        return configuration.getSymbolList();
    }

    // Other

    public Factors getFactors(Symbol symbol, int offset) {
        return barIterator.getFactors(symbol, offset);
    }

    public Factors getFactors(Identity identity, int offset) {
        return barIterator.getFactors(identity, offset);
    }

    public Position buy(Symbol symbol, LocalDateTime tradeTime, double price, double percentage, String remark) {
        return securitiesExchange.buy(symbol, tradeTime, price, percentage, remark);
    }

    public Position buySpecifiedQuantity(Symbol symbol, LocalDateTime tradeTime, double price, int quantity, String remark) {
        return securitiesExchange.buySpecifiedQuantity(symbol, tradeTime, price, quantity, remark);
    }

    public void sell(Symbol symbol, LocalDateTime tradeTime, double price, double percentage, String remark) {
        securitiesExchange.sell(symbol, tradeTime, price, percentage, remark);
    }

    void setSecuritiesExchange(SecuritiesExchangeAggregate securitiesExchange) {
        this.securitiesExchange = securitiesExchange;
        barIterator = securitiesExchange.getBarIterator();
        account = securitiesExchange.getAccountEntity();
    }

    public SecuritiesExchangeAggregate getSecuritiesExchange() {
        return securitiesExchange;
    }

    public AccountEntity getAccount() {
        return account;
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
