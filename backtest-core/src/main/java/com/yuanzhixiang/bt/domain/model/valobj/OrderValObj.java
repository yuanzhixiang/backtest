package com.yuanzhixiang.bt.domain.model.valobj;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * @author yuanzhixiang
 */
@Data
public class OrderValObj {

    public OrderValObj(LocalDateTime tradeDate, Symbol symbol, String side, double quantity, double price,
        double amount,
        double transferFee, double commissionFee, double stampDuty, double profit, String remark) {
        this.tradeDate = tradeDate;
        this.symbol = symbol;
        this.side = side;
        this.quantity = quantity;
        this.price = price;
        this.amount = amount;
        this.transferFee = transferFee;
        this.commissionFee = commissionFee;
        this.stampDuty = stampDuty;
        this.profit = profit;
        this.remark = remark;
    }

    private final LocalDateTime tradeDate;

    private final Symbol symbol;

    private final String side;

    private final double quantity;

    private final double price;

    private final double amount;

    private final double transferFee;

    private final double commissionFee;

    private final double stampDuty;

    private final double profit;

    private final String remark;

}
