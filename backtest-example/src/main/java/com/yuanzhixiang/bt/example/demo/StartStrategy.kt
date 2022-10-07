package com.yuanzhixiang.bt.example.demo

import com.yuanzhixiang.bt.service.CounterService
import com.yuanzhixiang.bt.engine.domain.Symbol
import com.yuanzhixiang.bt.engine.Configuration
import com.yuanzhixiang.bt.engine.Launcher
import com.yuanzhixiang.bt.example.report.DefaultReporter
import com.yuanzhixiang.bt.engine.Strategy
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

/**
 * @author Yuan Zhixiang
 */
object StartStrategy {
    private val log = LoggerFactory.getLogger(StartStrategy::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        // 实际运行策略
        application()
    }

    private fun application() {
        // 创建配置类
        val configuration = Configuration()

        // 初始资产
        configuration.counter = CounterService(
            0.001,
            0.0006,
            0.0003,
            1000000.0,
            true
        )

        // 设置回测时间范围
        configuration.setTimeRange(
            LocalDateTime.of(2020, 1, 1, 0, 0),
            LocalDateTime.now()
        )

        // 注册需要回测的标的
        val symbols: MutableList<Symbol> = ArrayList()
        symbols.add(Symbol("000001"))
        configuration.symbolList = symbols

        // 注册回测报告，回测完成后会在用户 Desktop 目录下创建回测报告
        configuration.registerLifeCycle(
            DefaultReporter(
                configuration
            )
        )
        log.info("Back test symbol count is [{}].", configuration.symbolList.size)

        // 创建策略类
        val strategy: Strategy = MacdStrategy(configuration)

        // 运行策略
        Launcher(configuration).start(strategy = strategy)
    }
}