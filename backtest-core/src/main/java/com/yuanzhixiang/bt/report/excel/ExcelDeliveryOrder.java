package com.yuanzhixiang.bt.report.excel;

import lombok.Data;

import java.time.LocalDateTime;

import com.yuanzhixiang.bt.domain.model.valobj.Symbol;

/**
 * @author yuanzhixiang
 */
@Data
public class ExcelDeliveryOrder {

    @ExcelColumn("交易日期")
    private LocalDateTime tradeDate;
    @ExcelColumn("操作方向")
    private String side;
    @ExcelColumn("标的编码")
    private Symbol symbol;
    @ExcelColumn("数量")
    private double quantity;
    @ExcelColumn("价格")
    private double price;
    @ExcelColumn("金额")
    private double amount;
    @ExcelColumn("盈利")
    private double profit;
    @ExcelColumn("过户费")
    private double transferFee;
    @ExcelColumn("佣金")
    private double commissionFee;
    @ExcelColumn("印花税")
    private double stampDuty;
    @ExcelColumn("备注")
    private String remark;
}
