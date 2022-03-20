package com.yuanzhixiang.bt.report.excel;


import lombok.Data;

/**
 * @author yuanzhixiang
 */
@Data
public class ExcelBasicReport {

    @ExcelColumn(value = "统计范围")
    private String range;
    @ExcelColumn(value = "相对初始资产收益率/%", percentage = true, statisticType = CalculateType.AVERAGE)
    private Double relationInitializeBalanceRateOfReturn;
    @ExcelColumn(value = "收益率/%", percentage = true, statisticType = CalculateType.AVERAGE)
    private Double finalRateOfReturn;
    @ExcelColumn(value = "年化收益率/%", percentage = true, statisticType = CalculateType.AVERAGE)
    private Double annualizedRateOfReturn;
    @ExcelColumn(value = "交易天数")
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
}
