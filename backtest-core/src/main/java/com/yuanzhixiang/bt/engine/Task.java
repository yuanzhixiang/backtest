package com.yuanzhixiang.bt.engine;

import com.yuanzhixiang.bt.engine.domain.Factors;
import com.yuanzhixiang.bt.engine.domain.Symbol;
import com.yuanzhixiang.bt.engine.market.IteratorWrapper;
import com.yuanzhixiang.bt.engine.market.Market;
import com.yuanzhixiang.bt.factor.common.Factor;
import com.yuanzhixiang.bt.factor.variant.VariantFactor;
import com.yuanzhixiang.bt.kit.DateKit;
import com.yuanzhixiang.bt.service.ContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Task implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Task.class);

    private final Configuration configuration;
    private final RunListeners runListeners;
    private final Strategy strategy;

    public Task(Configuration configuration, Strategy strategy) {
        this.configuration = configuration;
        this.runListeners = new RunListeners(configuration.getLifeCycleList());
        this.strategy = strategy;
    }

    @Override
    public void run() {
        log.info("Start loading data factor, symbol is {}.", configuration.getSymbolList());
        Map<Symbol, IteratorWrapper<Factors>> iteratorMap = queryFactors(configuration);

        try {
            // Initialize context
            ContextService contextService = new ContextService();
            ContextLocal.set(contextService);
            if (configuration.getCounter() == null) {
                throw new BackTestException("The counter must initialize");
            }
            contextService.setExchange(new ExchangeAggregate(configuration, configuration.getCounter()));
            contextService.getExchange().initializeQuotation(iteratorMap);

            // Publish application initialization
            runListeners.initialize(contextService);

            // Run strategy
            LocalDateTime startDate = configuration.getStartDate();
            LocalDateTime endDate = configuration.getEndDate();
            ExchangeAggregate.BarIterator barIterator = contextService.getExchange().getBarIterator();
            while (barIterator.hasNext()) {
                List<Factors> nextFactorsList = barIterator.next();

                for (Factors factors : nextFactorsList) {
                    bindFactor(contextService, factors);
                }

                // Only trade data within the specified time
                LocalDateTime tradingDate = nextFactorsList.get(0).getTradeDate();
                if (DateKit.notBetween(tradingDate, startDate, endDate)) {
                    continue;
                }

                // Publish strategy next
                runListeners.strategyNext(contextService, nextFactorsList);

                // Notify
                strategy.next(contextService, nextFactorsList);
            }

            // Publish strategy end
            log.debug("End of strategy.");
            runListeners.strategyEnd(contextService);
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


    private void bindFactor(ContextService contextService, Factors factors) {
        Collection<Factor<?>> factorList = configuration.getAllFactor();
        if (factorList.size() != 0) {
            for (Factor<?> factor : factorList) {
                factor.bind(contextService, factors);
            }
        }

        Collection<VariantFactor> variantFactorList = configuration.getVariantFactorMaps();
        if (variantFactorList.size() != 0) {
            for (VariantFactor variantFactor : configuration.getVariantFactorMaps()) {
                Factors periodFactors = variantFactor.bind(contextService, factors);
                for (Factor<?> factor : factorList) {
                    factor.bind(contextService, periodFactors);
                }
            }
        }
    }

    private static Map<Symbol, IteratorWrapper<Factors>> queryFactors(Configuration configuration) {
        List<Symbol> symbolList = configuration.getSymbolList();
        Market market = configuration.getDataSource();
        LocalDateTime startDate = configuration.getStartDate();
        LocalDateTime endDate = configuration.getEndDate();
        Map<Symbol, IteratorWrapper<Factors>> dataMap = new HashMap<>(symbolList.size());
        for (Symbol symbol : symbolList) {
            dataMap.put(symbol, new IteratorWrapper<>(market.queryFactors(symbol, startDate, endDate)));
        }
        return dataMap;
    }
}
