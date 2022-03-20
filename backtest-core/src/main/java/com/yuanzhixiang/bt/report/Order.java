package com.yuanzhixiang.bt.report;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author yuanzhixiang
 */
@Data
public class Order {

    private LocalDateTime tradeDate;

    private String symbol;

    private String side;

    private int quantity;

    private double price;

    private double amount;

    private double transferFee;

    private double commissionFee;

    private double stampDuty;

    private double profit;

}
