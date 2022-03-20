package com.yuanzhixiang.bt.domain.model.factory;

import com.yuanzhixiang.bt.domain.model.aggregate.SecuritiesExchangeAggregate;
import com.yuanzhixiang.bt.domain.model.entity.AccountEntity;
import com.yuanzhixiang.bt.engine.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yuanzhixiang
 */
@Slf4j
public class SecuritiesExchangeFactory {

    public static SecuritiesExchangeAggregate get(Configuration configuration) {
        // Initialize securities exchange.
        SecuritiesExchangeAggregate securitiesExchange = new SecuritiesExchangeAggregate(configuration, 0.001, 0.0006);

        // Initialize account.
        AccountEntity account = new AccountEntity();
        account.setBalance(configuration.getAccountBalance());
        securitiesExchange.registerAccount(account);

        return securitiesExchange;
    }
}
