package com.yuanzhixiang.bt.report.excel;

import lombok.Data;

/**
 * @author yuanzhixiang
 */
@Data
public class ExcelGroupBasicReport extends ExcelBasicReport {

    @ExcelColumn("标的编码")
    private String symbol;
}
