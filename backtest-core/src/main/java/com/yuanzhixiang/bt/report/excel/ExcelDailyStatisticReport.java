package com.yuanzhixiang.bt.report.excel;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author yuanzhixiang
 */
@Data
public class ExcelDailyStatisticReport {

    @ExcelColumn("交易日期")
    private LocalDateTime tradeDate;
    @ExcelColumn("账户资产")
    private double asset;
    @ExcelColumn("基准")
    private double benchmark;

    @ExcelColumn("回撤")
    private double drawdown;
}
