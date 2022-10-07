package com.yuanzhixiang.bt.example.report.excel;

import java.time.LocalDateTime;

/**
 * @author Yuan Zhixiang
 */
public class ExcelDailyStatisticReport {
    @ExcelColumn("交易日期")
    private LocalDateTime tradeDate;
    @ExcelColumn("账户资产")
    private double asset;
    @ExcelColumn("基准")
    private double benchmark;
    @ExcelColumn("回撤")
    private double drawdown;

    //<editor-fold defaultstate="collapsed" desc="delombok">
    @SuppressWarnings("all")
    public ExcelDailyStatisticReport() {
    }

    @SuppressWarnings("all")
    public LocalDateTime getTradeDate() {
        return this.tradeDate;
    }

    @SuppressWarnings("all")
    public double getAsset() {
        return this.asset;
    }

    @SuppressWarnings("all")
    public double getBenchmark() {
        return this.benchmark;
    }

    @SuppressWarnings("all")
    public double getDrawdown() {
        return this.drawdown;
    }

    @SuppressWarnings("all")
    public void setTradeDate(final LocalDateTime tradeDate) {
        this.tradeDate = tradeDate;
    }

    @SuppressWarnings("all")
    public void setAsset(final double asset) {
        this.asset = asset;
    }

    @SuppressWarnings("all")
    public void setBenchmark(final double benchmark) {
        this.benchmark = benchmark;
    }

    @SuppressWarnings("all")
    public void setDrawdown(final double drawdown) {
        this.drawdown = drawdown;
    }

    @Override
    @SuppressWarnings("all")
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ExcelDailyStatisticReport)) return false;
        final ExcelDailyStatisticReport other = (ExcelDailyStatisticReport) o;
        if (!other.canEqual((Object) this)) return false;
        if (Double.compare(this.getAsset(), other.getAsset()) != 0) return false;
        if (Double.compare(this.getBenchmark(), other.getBenchmark()) != 0) return false;
        if (Double.compare(this.getDrawdown(), other.getDrawdown()) != 0) return false;
        final Object this$tradeDate = this.getTradeDate();
        final Object other$tradeDate = other.getTradeDate();
        if (this$tradeDate == null ? other$tradeDate != null : !this$tradeDate.equals(other$tradeDate)) return false;
        return true;
    }

    @SuppressWarnings("all")
    protected boolean canEqual(final Object other) {
        return other instanceof ExcelDailyStatisticReport;
    }

    @Override
    @SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final long $asset = Double.doubleToLongBits(this.getAsset());
        result = result * PRIME + (int) ($asset >>> 32 ^ $asset);
        final long $benchmark = Double.doubleToLongBits(this.getBenchmark());
        result = result * PRIME + (int) ($benchmark >>> 32 ^ $benchmark);
        final long $drawdown = Double.doubleToLongBits(this.getDrawdown());
        result = result * PRIME + (int) ($drawdown >>> 32 ^ $drawdown);
        final Object $tradeDate = this.getTradeDate();
        result = result * PRIME + ($tradeDate == null ? 43 : $tradeDate.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings("all")
    public String toString() {
        return "ExcelDailyStatisticReport(tradeDate=" + this.getTradeDate() + ", asset=" + this.getAsset() + ", benchmark=" + this.getBenchmark() + ", drawdown=" + this.getDrawdown() + ")";
    }
    //</editor-fold>
}
