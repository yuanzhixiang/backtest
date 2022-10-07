package com.yuanzhixiang.bt.example.report.excel;

/**
 * @author Yuan Zhixiang
 */
public class ExcelBasicReport {
    @ExcelColumn("统计范围")
    private String range;
    @ExcelColumn(value = "相对初始资产收益率/%", percentage = true, statisticType = CalculateType.AVERAGE)
    private Double relationInitializeBalanceRateOfReturn;
    @ExcelColumn(value = "收益率/%", percentage = true, statisticType = CalculateType.AVERAGE)
    private Double finalRateOfReturn;
    @ExcelColumn(value = "年化收益率/%", percentage = true, statisticType = CalculateType.AVERAGE)
    private Double annualizedRateOfReturn;
    @ExcelColumn("交易天数")
    private Integer tradeDay;
    @ExcelColumn(value = "胜率/%", percentage = true, statisticType = CalculateType.AVERAGE)
    private Double winRate;
    @ExcelColumn(value = "获胜次数/次", statisticType = CalculateType.AVERAGE)
    private Integer numberOfVictories;
    @ExcelColumn(value = "失败次数/次", statisticType = CalculateType.AVERAGE)
    private Integer numberOfFailures;
    @ExcelColumn(value = "总交易次数/次", statisticType = CalculateType.AVERAGE)
    private Integer totalNumberOfTransactions;
    @ExcelColumn(value = "平均每月交易次数/次", statisticType = CalculateType.AVERAGE)
    private Double averageNumberOfTransactionsPerMonth;
    @ExcelColumn(value = "平均每年交易次数/次", statisticType = CalculateType.AVERAGE)
    private Double averageNumberOfTransactionsPerYear;
    @ExcelColumn(value = "盈亏比", statisticType = CalculateType.AVERAGE)
    private Double profitLossRatio;
    @ExcelColumn(value = "夏普比率", statisticType = CalculateType.AVERAGE)
    private Double sharpeRatio;
    @ExcelColumn(value = "最高收益率/%", percentage = true, statisticType = CalculateType.MAX)
    private Double maximumRateOfReturn;
    @ExcelColumn(value = "最高回撤/%", percentage = true, statisticType = CalculateType.MAX)
    private Double maximumDrawdownRate;
    @ExcelColumn(value = "最长回撤时间/天", statisticType = CalculateType.MAX)
    private Integer maximumDrawdownTime;

    //<editor-fold defaultstate="collapsed" desc="delombok">
    @SuppressWarnings("all")
    public ExcelBasicReport() {
    }

    @SuppressWarnings("all")
    public String getRange() {
        return this.range;
    }

    @SuppressWarnings("all")
    public Double getRelationInitializeBalanceRateOfReturn() {
        return this.relationInitializeBalanceRateOfReturn;
    }

    @SuppressWarnings("all")
    public Double getFinalRateOfReturn() {
        return this.finalRateOfReturn;
    }

    @SuppressWarnings("all")
    public Double getAnnualizedRateOfReturn() {
        return this.annualizedRateOfReturn;
    }

    @SuppressWarnings("all")
    public Integer getTradeDay() {
        return this.tradeDay;
    }

    @SuppressWarnings("all")
    public Double getWinRate() {
        return this.winRate;
    }

    @SuppressWarnings("all")
    public Integer getNumberOfVictories() {
        return this.numberOfVictories;
    }

    @SuppressWarnings("all")
    public Integer getNumberOfFailures() {
        return this.numberOfFailures;
    }

    @SuppressWarnings("all")
    public Integer getTotalNumberOfTransactions() {
        return this.totalNumberOfTransactions;
    }

    @SuppressWarnings("all")
    public Double getAverageNumberOfTransactionsPerMonth() {
        return this.averageNumberOfTransactionsPerMonth;
    }

    @SuppressWarnings("all")
    public Double getAverageNumberOfTransactionsPerYear() {
        return this.averageNumberOfTransactionsPerYear;
    }

    @SuppressWarnings("all")
    public Double getProfitLossRatio() {
        return this.profitLossRatio;
    }

    @SuppressWarnings("all")
    public Double getSharpeRatio() {
        return this.sharpeRatio;
    }

    @SuppressWarnings("all")
    public Double getMaximumRateOfReturn() {
        return this.maximumRateOfReturn;
    }

    @SuppressWarnings("all")
    public Double getMaximumDrawdownRate() {
        return this.maximumDrawdownRate;
    }

    @SuppressWarnings("all")
    public Integer getMaximumDrawdownTime() {
        return this.maximumDrawdownTime;
    }

    @SuppressWarnings("all")
    public void setRange(final String range) {
        this.range = range;
    }

    @SuppressWarnings("all")
    public void setRelationInitializeBalanceRateOfReturn(final Double relationInitializeBalanceRateOfReturn) {
        this.relationInitializeBalanceRateOfReturn = relationInitializeBalanceRateOfReturn;
    }

    @SuppressWarnings("all")
    public void setFinalRateOfReturn(final Double finalRateOfReturn) {
        this.finalRateOfReturn = finalRateOfReturn;
    }

    @SuppressWarnings("all")
    public void setAnnualizedRateOfReturn(final Double annualizedRateOfReturn) {
        this.annualizedRateOfReturn = annualizedRateOfReturn;
    }

    @SuppressWarnings("all")
    public void setTradeDay(final Integer tradeDay) {
        this.tradeDay = tradeDay;
    }

    @SuppressWarnings("all")
    public void setWinRate(final Double winRate) {
        this.winRate = winRate;
    }

    @SuppressWarnings("all")
    public void setNumberOfVictories(final Integer numberOfVictories) {
        this.numberOfVictories = numberOfVictories;
    }

    @SuppressWarnings("all")
    public void setNumberOfFailures(final Integer numberOfFailures) {
        this.numberOfFailures = numberOfFailures;
    }

    @SuppressWarnings("all")
    public void setTotalNumberOfTransactions(final Integer totalNumberOfTransactions) {
        this.totalNumberOfTransactions = totalNumberOfTransactions;
    }

    @SuppressWarnings("all")
    public void setAverageNumberOfTransactionsPerMonth(final Double averageNumberOfTransactionsPerMonth) {
        this.averageNumberOfTransactionsPerMonth = averageNumberOfTransactionsPerMonth;
    }

    @SuppressWarnings("all")
    public void setAverageNumberOfTransactionsPerYear(final Double averageNumberOfTransactionsPerYear) {
        this.averageNumberOfTransactionsPerYear = averageNumberOfTransactionsPerYear;
    }

    @SuppressWarnings("all")
    public void setProfitLossRatio(final Double profitLossRatio) {
        this.profitLossRatio = profitLossRatio;
    }

    @SuppressWarnings("all")
    public void setSharpeRatio(final Double sharpeRatio) {
        this.sharpeRatio = sharpeRatio;
    }

    @SuppressWarnings("all")
    public void setMaximumRateOfReturn(final Double maximumRateOfReturn) {
        this.maximumRateOfReturn = maximumRateOfReturn;
    }

    @SuppressWarnings("all")
    public void setMaximumDrawdownRate(final Double maximumDrawdownRate) {
        this.maximumDrawdownRate = maximumDrawdownRate;
    }

    @SuppressWarnings("all")
    public void setMaximumDrawdownTime(final Integer maximumDrawdownTime) {
        this.maximumDrawdownTime = maximumDrawdownTime;
    }

    @Override
    @SuppressWarnings("all")
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ExcelBasicReport)) return false;
        final ExcelBasicReport other = (ExcelBasicReport) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$relationInitializeBalanceRateOfReturn = this.getRelationInitializeBalanceRateOfReturn();
        final Object other$relationInitializeBalanceRateOfReturn = other.getRelationInitializeBalanceRateOfReturn();
        if (this$relationInitializeBalanceRateOfReturn == null ? other$relationInitializeBalanceRateOfReturn != null : !this$relationInitializeBalanceRateOfReturn.equals(other$relationInitializeBalanceRateOfReturn)) return false;
        final Object this$finalRateOfReturn = this.getFinalRateOfReturn();
        final Object other$finalRateOfReturn = other.getFinalRateOfReturn();
        if (this$finalRateOfReturn == null ? other$finalRateOfReturn != null : !this$finalRateOfReturn.equals(other$finalRateOfReturn)) return false;
        final Object this$annualizedRateOfReturn = this.getAnnualizedRateOfReturn();
        final Object other$annualizedRateOfReturn = other.getAnnualizedRateOfReturn();
        if (this$annualizedRateOfReturn == null ? other$annualizedRateOfReturn != null : !this$annualizedRateOfReturn.equals(other$annualizedRateOfReturn)) return false;
        final Object this$tradeDay = this.getTradeDay();
        final Object other$tradeDay = other.getTradeDay();
        if (this$tradeDay == null ? other$tradeDay != null : !this$tradeDay.equals(other$tradeDay)) return false;
        final Object this$winRate = this.getWinRate();
        final Object other$winRate = other.getWinRate();
        if (this$winRate == null ? other$winRate != null : !this$winRate.equals(other$winRate)) return false;
        final Object this$numberOfVictories = this.getNumberOfVictories();
        final Object other$numberOfVictories = other.getNumberOfVictories();
        if (this$numberOfVictories == null ? other$numberOfVictories != null : !this$numberOfVictories.equals(other$numberOfVictories)) return false;
        final Object this$numberOfFailures = this.getNumberOfFailures();
        final Object other$numberOfFailures = other.getNumberOfFailures();
        if (this$numberOfFailures == null ? other$numberOfFailures != null : !this$numberOfFailures.equals(other$numberOfFailures)) return false;
        final Object this$totalNumberOfTransactions = this.getTotalNumberOfTransactions();
        final Object other$totalNumberOfTransactions = other.getTotalNumberOfTransactions();
        if (this$totalNumberOfTransactions == null ? other$totalNumberOfTransactions != null : !this$totalNumberOfTransactions.equals(other$totalNumberOfTransactions)) return false;
        final Object this$averageNumberOfTransactionsPerMonth = this.getAverageNumberOfTransactionsPerMonth();
        final Object other$averageNumberOfTransactionsPerMonth = other.getAverageNumberOfTransactionsPerMonth();
        if (this$averageNumberOfTransactionsPerMonth == null ? other$averageNumberOfTransactionsPerMonth != null : !this$averageNumberOfTransactionsPerMonth.equals(other$averageNumberOfTransactionsPerMonth)) return false;
        final Object this$averageNumberOfTransactionsPerYear = this.getAverageNumberOfTransactionsPerYear();
        final Object other$averageNumberOfTransactionsPerYear = other.getAverageNumberOfTransactionsPerYear();
        if (this$averageNumberOfTransactionsPerYear == null ? other$averageNumberOfTransactionsPerYear != null : !this$averageNumberOfTransactionsPerYear.equals(other$averageNumberOfTransactionsPerYear)) return false;
        final Object this$profitLossRatio = this.getProfitLossRatio();
        final Object other$profitLossRatio = other.getProfitLossRatio();
        if (this$profitLossRatio == null ? other$profitLossRatio != null : !this$profitLossRatio.equals(other$profitLossRatio)) return false;
        final Object this$sharpeRatio = this.getSharpeRatio();
        final Object other$sharpeRatio = other.getSharpeRatio();
        if (this$sharpeRatio == null ? other$sharpeRatio != null : !this$sharpeRatio.equals(other$sharpeRatio)) return false;
        final Object this$maximumRateOfReturn = this.getMaximumRateOfReturn();
        final Object other$maximumRateOfReturn = other.getMaximumRateOfReturn();
        if (this$maximumRateOfReturn == null ? other$maximumRateOfReturn != null : !this$maximumRateOfReturn.equals(other$maximumRateOfReturn)) return false;
        final Object this$maximumDrawdownRate = this.getMaximumDrawdownRate();
        final Object other$maximumDrawdownRate = other.getMaximumDrawdownRate();
        if (this$maximumDrawdownRate == null ? other$maximumDrawdownRate != null : !this$maximumDrawdownRate.equals(other$maximumDrawdownRate)) return false;
        final Object this$maximumDrawdownTime = this.getMaximumDrawdownTime();
        final Object other$maximumDrawdownTime = other.getMaximumDrawdownTime();
        if (this$maximumDrawdownTime == null ? other$maximumDrawdownTime != null : !this$maximumDrawdownTime.equals(other$maximumDrawdownTime)) return false;
        final Object this$range = this.getRange();
        final Object other$range = other.getRange();
        if (this$range == null ? other$range != null : !this$range.equals(other$range)) return false;
        return true;
    }

    @SuppressWarnings("all")
    protected boolean canEqual(final Object other) {
        return other instanceof ExcelBasicReport;
    }

    @Override
    @SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $relationInitializeBalanceRateOfReturn = this.getRelationInitializeBalanceRateOfReturn();
        result = result * PRIME + ($relationInitializeBalanceRateOfReturn == null ? 43 : $relationInitializeBalanceRateOfReturn.hashCode());
        final Object $finalRateOfReturn = this.getFinalRateOfReturn();
        result = result * PRIME + ($finalRateOfReturn == null ? 43 : $finalRateOfReturn.hashCode());
        final Object $annualizedRateOfReturn = this.getAnnualizedRateOfReturn();
        result = result * PRIME + ($annualizedRateOfReturn == null ? 43 : $annualizedRateOfReturn.hashCode());
        final Object $tradeDay = this.getTradeDay();
        result = result * PRIME + ($tradeDay == null ? 43 : $tradeDay.hashCode());
        final Object $winRate = this.getWinRate();
        result = result * PRIME + ($winRate == null ? 43 : $winRate.hashCode());
        final Object $numberOfVictories = this.getNumberOfVictories();
        result = result * PRIME + ($numberOfVictories == null ? 43 : $numberOfVictories.hashCode());
        final Object $numberOfFailures = this.getNumberOfFailures();
        result = result * PRIME + ($numberOfFailures == null ? 43 : $numberOfFailures.hashCode());
        final Object $totalNumberOfTransactions = this.getTotalNumberOfTransactions();
        result = result * PRIME + ($totalNumberOfTransactions == null ? 43 : $totalNumberOfTransactions.hashCode());
        final Object $averageNumberOfTransactionsPerMonth = this.getAverageNumberOfTransactionsPerMonth();
        result = result * PRIME + ($averageNumberOfTransactionsPerMonth == null ? 43 : $averageNumberOfTransactionsPerMonth.hashCode());
        final Object $averageNumberOfTransactionsPerYear = this.getAverageNumberOfTransactionsPerYear();
        result = result * PRIME + ($averageNumberOfTransactionsPerYear == null ? 43 : $averageNumberOfTransactionsPerYear.hashCode());
        final Object $profitLossRatio = this.getProfitLossRatio();
        result = result * PRIME + ($profitLossRatio == null ? 43 : $profitLossRatio.hashCode());
        final Object $sharpeRatio = this.getSharpeRatio();
        result = result * PRIME + ($sharpeRatio == null ? 43 : $sharpeRatio.hashCode());
        final Object $maximumRateOfReturn = this.getMaximumRateOfReturn();
        result = result * PRIME + ($maximumRateOfReturn == null ? 43 : $maximumRateOfReturn.hashCode());
        final Object $maximumDrawdownRate = this.getMaximumDrawdownRate();
        result = result * PRIME + ($maximumDrawdownRate == null ? 43 : $maximumDrawdownRate.hashCode());
        final Object $maximumDrawdownTime = this.getMaximumDrawdownTime();
        result = result * PRIME + ($maximumDrawdownTime == null ? 43 : $maximumDrawdownTime.hashCode());
        final Object $range = this.getRange();
        result = result * PRIME + ($range == null ? 43 : $range.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings("all")
    public String toString() {
        return "ExcelBasicReport(range=" + this.getRange() + ", relationInitializeBalanceRateOfReturn=" + this.getRelationInitializeBalanceRateOfReturn() + ", finalRateOfReturn=" + this.getFinalRateOfReturn() + ", annualizedRateOfReturn=" + this.getAnnualizedRateOfReturn() + ", tradeDay=" + this.getTradeDay() + ", winRate=" + this.getWinRate() + ", numberOfVictories=" + this.getNumberOfVictories() + ", numberOfFailures=" + this.getNumberOfFailures() + ", totalNumberOfTransactions=" + this.getTotalNumberOfTransactions() + ", averageNumberOfTransactionsPerMonth=" + this.getAverageNumberOfTransactionsPerMonth() + ", averageNumberOfTransactionsPerYear=" + this.getAverageNumberOfTransactionsPerYear() + ", profitLossRatio=" + this.getProfitLossRatio() + ", sharpeRatio=" + this.getSharpeRatio() + ", maximumRateOfReturn=" + this.getMaximumRateOfReturn() + ", maximumDrawdownRate=" + this.getMaximumDrawdownRate() + ", maximumDrawdownTime=" + this.getMaximumDrawdownTime() + ")";
    }
    //</editor-fold>
}
