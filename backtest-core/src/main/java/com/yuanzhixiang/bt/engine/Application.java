package com.yuanzhixiang.bt.engine;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yuanzhixiang.bt.domain.model.aggregate.SecuritiesExchangeAggregate.BarIterator;
import com.yuanzhixiang.bt.domain.model.factory.SecuritiesExchangeFactory;
import com.yuanzhixiang.bt.domain.model.valobj.Factors;
import com.yuanzhixiang.bt.domain.model.valobj.Symbol;
import com.yuanzhixiang.bt.domain.repository.DataSource;
import com.yuanzhixiang.bt.domain.repository.IteratorWrapper;
import com.yuanzhixiang.bt.exception.BackTestException;
import com.yuanzhixiang.bt.factor.common.Factor;
import com.yuanzhixiang.bt.factor.variant.VariantFactor;
import com.yuanzhixiang.bt.strategy.Strategy;
import com.yuanzhixiang.bt.kit.DateKit;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yuanzhixiang
 */
@Slf4j
public class Application {

    public Application(Configuration configuration) {
        if (configuration.getDataSource() == null) {
            throw new BackTestException("Uninitialized data source.");
        }
        this.configuration = configuration.deepClone();
        runListeners = new RunListeners(configuration.getLifeCycleList());
    }

    private final Configuration configuration;

    private final RunListeners runListeners;

    public void run(Strategy strategy) {
        if (strategy == null) {
            throw new BackTestException("Uninitialized backtesting strategy.");
        }



        log.info("Start loading data factor, symbol is {}.", configuration.getSymbolList());
        Map<Symbol, IteratorWrapper<Factors>> iteratorMap = queryFactors(configuration);

        try {
            // Initialize context
            Context context = new Context(configuration);
            ContextLocal.set(context);
            context.setSecuritiesExchange(SecuritiesExchangeFactory.get(configuration));
            context.getSecuritiesExchange().initializeQuotation(iteratorMap);

            // Publish application initialization
            runListeners.initialize(context);

            // Run strategy
            LocalDateTime startDate = configuration.getStartDate();
            LocalDateTime endDate = configuration.getEndDate();
            BarIterator barIterator = context.getSecuritiesExchange().getBarIterator();
            while (barIterator.hasNext()) {
                List<Factors> nextFactorsList = barIterator.next();

                for (Factors factors : nextFactorsList) {
                    bindFactor(context, factors);
                }

                // Only trade data within the specified time
                LocalDateTime tradingDate = nextFactorsList.get(0).getTradeDate();
                if (DateKit.notBetween(tradingDate, startDate, endDate)) {
                    continue;
                }

                // Publish strategy next
                runListeners.strategyNext(context, nextFactorsList);

                // Notify
                strategy.next(context, nextFactorsList);
            }

            // Publish strategy end
            log.debug("End of strategy.");
            runListeners.strategyEnd(context);
        } catch (Exception e) {
            log.error("Failed when running {}.", configuration.getSymbolList(), e);
            throw new BackTestException(e);
        } finally {
            ContextLocal.remove();
            iteratorMap.forEach((symbol, factorsEnhancedIterator) -> {
                try {
                    factorsEnhancedIterator.close();
                } catch (Exception e) {
                    log.error("Close iterator error.", e);
                }
            });
        }
    }

    private void bindFactor(Context context, Factors factors) {
        Collection<Factor<?>> factorList = configuration.getAllFactor();
        if (factorList.size() != 0) {
            for (Factor<?> factor : factorList) {
                factor.bind(context, factors);
            }
        }

        Collection<VariantFactor> variantFactorList = configuration.getVariantFactorMaps();
        if (variantFactorList.size() != 0) {
            for (VariantFactor variantFactor : configuration.getVariantFactorMaps()) {
                Factors periodFactors = variantFactor.bind(context, factors);
                for (Factor<?> factor : factorList) {
                    factor.bind(context, periodFactors);
                }
            }
        }
    }

    private static Map<Symbol, IteratorWrapper<Factors>> queryFactors(Configuration configuration) {
        List<Symbol> symbolList = configuration.getSymbolList();
        DataSource dataSource = configuration.getDataSource();
        LocalDateTime startDate = configuration.getStartDate();
        LocalDateTime endDate = configuration.getEndDate();
        Map<Symbol, IteratorWrapper<Factors>> dataMap = new HashMap<>(symbolList.size());
        for (Symbol symbol : symbolList) {
            dataMap.put(symbol, new IteratorWrapper<>(dataSource.queryFactors(symbol, startDate, endDate)));
        }
        return dataMap;
    }
}
