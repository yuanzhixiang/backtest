package com.yuanzhixiang.bt.engine.domain;

import com.yuanzhixiang.bt.engine.SideEnum;

import java.time.LocalDateTime;

/**
 * @author Yuan Zhixiang
 */
public class TradeRecord {

    public TradeRecord(LocalDateTime tradeDate, Symbol symbol, SideEnum side, double quantity, double price,
                       double amount, double transferFee, double commissionFee, double stampDuty, double profit) {
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
    }

    private final LocalDateTime tradeDate;

    private final Symbol symbol;

    private final SideEnum side;

    private final double quantity;

    private final double price;

    private final double amount;

    private final double transferFee;

    private final double commissionFee;

    private final double stampDuty;

    private final double profit;

    public LocalDateTime getTradeDate() {
        return tradeDate;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public SideEnum getSide() {
        return side;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getAmount() {
        return amount;
    }

    public double getTransferFee() {
        return transferFee;
    }

    public double getCommissionFee() {
        return commissionFee;
    }

    public double getStampDuty() {
        return stampDuty;
    }

    public double getProfit() {
        return profit;
    }

}
