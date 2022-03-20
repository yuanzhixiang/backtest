package com.yuanzhixiang.bt.example.demo;

import com.yuanzhixiang.bt.domain.model.valobj.Factors;
import com.yuanzhixiang.bt.engine.Configuration;
import com.yuanzhixiang.bt.engine.Context;
import com.yuanzhixiang.bt.engine.ContextLocal;
import com.yuanzhixiang.bt.example.datasource.DataSourceStockDaily;
import com.yuanzhixiang.bt.factor.common.FactorMacd;
import com.yuanzhixiang.bt.factor.common.FactorMacd.Macd;
import com.yuanzhixiang.bt.factor.variant.PeriodFactorDaily;
import com.yuanzhixiang.bt.strategy.Strategy;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yuanzhixiang
 */
@Slf4j
public class MacdStrategy implements Strategy {

    public MacdStrategy(Configuration configuration) {
        // 注册日线级别数据源
        configuration.setDataSource(new DataSourceStockDaily());
        // 注册日线因子，注册该因子后框架会自动计算日线，则在策略中可以使用日线数据
        configuration.registerPeriodFactor(new PeriodFactorDaily());
        // 注册 Macd 因子，注册该因子后框架会自动计算 Macd，则在策略中可以使用 Macd 数据
        configuration.registerFactor(new FactorMacd());
    }

    @Override
    public void next(Context context, Factors factors) {
        // 由于 factors 是分钟因子，所以这里借助 PeriodFactorDaily 获取到日线因子
        Factors dailyFactors = PeriodFactorDaily.get(factors, 0);

        // 从日线因子上获取到 Macd 因子，-1 表示获取昨天的日线因子，-2 表示获取前天的日线因子
        Macd pDailyMacd = FactorMacd.get(dailyFactors, -1);
        Macd ppDailyMacd = FactorMacd.get(dailyFactors, -2);

        // 对于刚上市的标的可能取不到需要判空，为空则直接返回
        if (pDailyMacd == null || ppDailyMacd == null) {
            if (pDailyMacd == null) {
                log.info("pDailyMacd is null");
            }
            if (ppDailyMacd == null) {
                log.info("ppDailyMacd is null");
            }
            return;
        }

        // 判断金叉则买入
        if (pDailyMacd.getDiff() > pDailyMacd.getDea() && ppDailyMacd.getDiff() < ppDailyMacd.getDea()) {
            log.info("buy: {} {} {}", dailyFactors.getSymbol(), dailyFactors.getTradeDate(), dailyFactors.getClose().getPrice());
            // 这里为了方便所以仓位设置为 1，表示全仓买入
            ContextLocal.get().buy(factors.getSymbol(), factors.getTradeDate(), dailyFactors.getOpen().getPrice(), 1, "昨天 macd 金叉，今天以开盘价买入");
        }

        // 判断死叉则卖出
        if (pDailyMacd.getDiff() < pDailyMacd.getDea() && ppDailyMacd.getDiff() > ppDailyMacd.getDea()) {
            // 这里为了方便所以仓位设置为 1，表示全仓卖出
            context.sell(factors.getSymbol(), factors.getTradeDate(), dailyFactors.getOpen().getPrice(), 1, "昨天 macd 死叉，今天以开盘价卖出");
        }
    }
}
