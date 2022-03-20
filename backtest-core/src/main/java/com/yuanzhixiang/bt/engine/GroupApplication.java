package com.yuanzhixiang.bt.engine;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.yuanzhixiang.bt.domain.model.valobj.Symbol;
import com.yuanzhixiang.bt.report.GroupReporter;
import com.yuanzhixiang.bt.strategy.Strategy;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yuanzhixiang
 */
@Slf4j
public class GroupApplication {

    private final Configuration configuration;

    public GroupApplication(Configuration configuration) {
        this.configuration = configuration;
    }

    public void run(Strategy strategy) {
        List<Symbol> symbolList = configuration.getSymbolList();

        log.info("The total number of back-testing targets is {}.", symbolList.size());
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(20, 20, 0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1000000), new ThreadPoolExecutor.AbortPolicy());

        AtomicInteger count = new AtomicInteger(0);

        for (int index = 0; index < symbolList.size(); index++) {

            int finalIndex = index;

            threadPoolExecutor.execute(() -> {
                log.info("The total number is {}, and the {} task is currently started.", symbolList.size(), finalIndex + 1);
                Symbol symbol = symbolList.get(finalIndex);

                Configuration configuration = this.configuration.deepClone();
                configuration.setSymbolList(Collections.singletonList(symbol));

                try {
                    new Application(configuration).run(strategy);
                } catch (Exception exception) {
                    log.warn("An exception occurred when running the task, and the symbol code is {}.", symbol, exception);
                } finally {
                    count.incrementAndGet();
                }
            });
        }

        while (count.get() != symbolList.size()) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                log.error("", e);
            }
        }

        threadPoolExecutor.shutdown();

        if (CollectionUtil.isNotEmpty(configuration.getLifeCycleList())) {
            for (LifeCycle reporter : configuration.getLifeCycleList()) {
                if (reporter instanceof GroupReporter) {
                    ((GroupReporter) reporter).generate();
                }
            }
        }

        log.info("All back-test tasks have been completed, and the back-test report is generated.");
    }
}
