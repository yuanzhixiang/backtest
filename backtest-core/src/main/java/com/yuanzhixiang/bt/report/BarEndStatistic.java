package com.yuanzhixiang.bt.report;

import java.time.LocalDateTime;

/**
 * @author yuanzhixiang
 */
public class BarEndStatistic {

    public BarEndStatistic(LocalDateTime dateTime, double asset, double drawdown, double benchmarkPrice) {
        this.dateTime = dateTime;
        this.asset = asset;
        this.drawdown = drawdown;
        this.benchmarkPrice = benchmarkPrice;
    }

    private final LocalDateTime dateTime;

    private final double asset;

    private final double drawdown;

    private final double benchmarkPrice;

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public double getAsset() {
        return asset;
    }

    public double getDrawdown() {
        return drawdown;
    }

    public double getBenchmarkPrice() {
        return benchmarkPrice;
    }
}
