package com.yuanzhixiang.bt.example.demo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.yuanzhixiang.bt.domain.model.valobj.Symbol;
import com.yuanzhixiang.bt.engine.Application;
import com.yuanzhixiang.bt.engine.Configuration;
import com.yuanzhixiang.bt.report.DefaultReporter;
import com.yuanzhixiang.bt.strategy.Strategy;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yuanzhixiang
 */
@Slf4j
public class StartStrategy {

    public static void main(String[] args) {
        // 实际运行策略
        application();
    }

    public static void application() {
        // 创建配置类
        Configuration configuration = new Configuration();

        // 手续费万三
        configuration.setCommissionRate(0.0003);
        // 初始资产
        configuration.setAccountBalance(1000000);
        // 开启 T+1 交易限制
        configuration.setTodayAddOneTrade(true);
        // 注册回测报告，回测完成后会在用户 Desktop 目录下创建回测报告
        configuration.registerLifeCycle(new DefaultReporter());

        // 设置回测时间范围
        configuration.setTimeRange(
            LocalDateTime.of(2020, 1, 1, 0, 0),
            LocalDateTime.now()
        );

        // 注册需要回测的标的
        List<Symbol> symbols = new ArrayList<>();
        symbols.add(new Symbol("000001"));
        configuration.setSymbolList(symbols);

        log.info("Back test symbol count is [{}].", configuration.getSymbolList().size());

        // 创建策略类
        Strategy strategy = new MacdStrategy(configuration);

        // 运行策略
        new Application(configuration).run(strategy);
    }
}
