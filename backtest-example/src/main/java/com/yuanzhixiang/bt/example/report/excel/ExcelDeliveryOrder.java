package com.yuanzhixiang.bt.example.report.excel;

import java.time.LocalDateTime;
import com.yuanzhixiang.bt.engine.domain.Symbol;

/**
 * @author Yuan Zhixiang
 */
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

    //<editor-fold defaultstate="collapsed" desc="delombok">
    @SuppressWarnings("all")
    public ExcelDeliveryOrder() {
    }

    @SuppressWarnings("all")
    public LocalDateTime getTradeDate() {
        return this.tradeDate;
    }

    @SuppressWarnings("all")
    public String getSide() {
        return this.side;
    }

    @SuppressWarnings("all")
    public Symbol getSymbol() {
        return this.symbol;
    }

    @SuppressWarnings("all")
    public double getQuantity() {
        return this.quantity;
    }

    @SuppressWarnings("all")
    public double getPrice() {
        return this.price;
    }

    @SuppressWarnings("all")
    public double getAmount() {
        return this.amount;
    }

    @SuppressWarnings("all")
    public double getProfit() {
        return this.profit;
    }

    @SuppressWarnings("all")
    public double getTransferFee() {
        return this.transferFee;
    }

    @SuppressWarnings("all")
    public double getCommissionFee() {
        return this.commissionFee;
    }

    @SuppressWarnings("all")
    public double getStampDuty() {
        return this.stampDuty;
    }

    @SuppressWarnings("all")
    public String getRemark() {
        return this.remark;
    }

    @SuppressWarnings("all")
    public void setTradeDate(final LocalDateTime tradeDate) {
        this.tradeDate = tradeDate;
    }

    @SuppressWarnings("all")
    public void setSide(final String side) {
        this.side = side;
    }

    @SuppressWarnings("all")
    public void setSymbol(final Symbol symbol) {
        this.symbol = symbol;
    }

    @SuppressWarnings("all")
    public void setQuantity(final double quantity) {
        this.quantity = quantity;
    }

    @SuppressWarnings("all")
    public void setPrice(final double price) {
        this.price = price;
    }

    @SuppressWarnings("all")
    public void setAmount(final double amount) {
        this.amount = amount;
    }

    @SuppressWarnings("all")
    public void setProfit(final double profit) {
        this.profit = profit;
    }

    @SuppressWarnings("all")
    public void setTransferFee(final double transferFee) {
        this.transferFee = transferFee;
    }

    @SuppressWarnings("all")
    public void setCommissionFee(final double commissionFee) {
        this.commissionFee = commissionFee;
    }

    @SuppressWarnings("all")
    public void setStampDuty(final double stampDuty) {
        this.stampDuty = stampDuty;
    }

    @SuppressWarnings("all")
    public void setRemark(final String remark) {
        this.remark = remark;
    }

    @Override
    @SuppressWarnings("all")
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ExcelDeliveryOrder)) return false;
        final ExcelDeliveryOrder other = (ExcelDeliveryOrder) o;
        if (!other.canEqual((Object) this)) return false;
        if (Double.compare(this.getQuantity(), other.getQuantity()) != 0) return false;
        if (Double.compare(this.getPrice(), other.getPrice()) != 0) return false;
        if (Double.compare(this.getAmount(), other.getAmount()) != 0) return false;
        if (Double.compare(this.getProfit(), other.getProfit()) != 0) return false;
        if (Double.compare(this.getTransferFee(), other.getTransferFee()) != 0) return false;
        if (Double.compare(this.getCommissionFee(), other.getCommissionFee()) != 0) return false;
        if (Double.compare(this.getStampDuty(), other.getStampDuty()) != 0) return false;
        final Object this$tradeDate = this.getTradeDate();
        final Object other$tradeDate = other.getTradeDate();
        if (this$tradeDate == null ? other$tradeDate != null : !this$tradeDate.equals(other$tradeDate)) return false;
        final Object this$side = this.getSide();
        final Object other$side = other.getSide();
        if (this$side == null ? other$side != null : !this$side.equals(other$side)) return false;
        final Object this$symbol = this.getSymbol();
        final Object other$symbol = other.getSymbol();
        if (this$symbol == null ? other$symbol != null : !this$symbol.equals(other$symbol)) return false;
        final Object this$remark = this.getRemark();
        final Object other$remark = other.getRemark();
        if (this$remark == null ? other$remark != null : !this$remark.equals(other$remark)) return false;
        return true;
    }

    @SuppressWarnings("all")
    protected boolean canEqual(final Object other) {
        return other instanceof ExcelDeliveryOrder;
    }

    @Override
    @SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final long $quantity = Double.doubleToLongBits(this.getQuantity());
        result = result * PRIME + (int) ($quantity >>> 32 ^ $quantity);
        final long $price = Double.doubleToLongBits(this.getPrice());
        result = result * PRIME + (int) ($price >>> 32 ^ $price);
        final long $amount = Double.doubleToLongBits(this.getAmount());
        result = result * PRIME + (int) ($amount >>> 32 ^ $amount);
        final long $profit = Double.doubleToLongBits(this.getProfit());
        result = result * PRIME + (int) ($profit >>> 32 ^ $profit);
        final long $transferFee = Double.doubleToLongBits(this.getTransferFee());
        result = result * PRIME + (int) ($transferFee >>> 32 ^ $transferFee);
        final long $commissionFee = Double.doubleToLongBits(this.getCommissionFee());
        result = result * PRIME + (int) ($commissionFee >>> 32 ^ $commissionFee);
        final long $stampDuty = Double.doubleToLongBits(this.getStampDuty());
        result = result * PRIME + (int) ($stampDuty >>> 32 ^ $stampDuty);
        final Object $tradeDate = this.getTradeDate();
        result = result * PRIME + ($tradeDate == null ? 43 : $tradeDate.hashCode());
        final Object $side = this.getSide();
        result = result * PRIME + ($side == null ? 43 : $side.hashCode());
        final Object $symbol = this.getSymbol();
        result = result * PRIME + ($symbol == null ? 43 : $symbol.hashCode());
        final Object $remark = this.getRemark();
        result = result * PRIME + ($remark == null ? 43 : $remark.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings("all")
    public String toString() {
        return "ExcelDeliveryOrder(tradeDate=" + this.getTradeDate() + ", side=" + this.getSide() + ", symbol=" + this.getSymbol() + ", quantity=" + this.getQuantity() + ", price=" + this.getPrice() + ", amount=" + this.getAmount() + ", profit=" + this.getProfit() + ", transferFee=" + this.getTransferFee() + ", commissionFee=" + this.getCommissionFee() + ", stampDuty=" + this.getStampDuty() + ", remark=" + this.getRemark() + ")";
    }
    //</editor-fold>
}
