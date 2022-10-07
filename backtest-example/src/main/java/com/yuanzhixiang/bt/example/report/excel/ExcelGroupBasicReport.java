package com.yuanzhixiang.bt.example.report.excel;

/**
 * @author Yuan Zhixiang
 */
public class ExcelGroupBasicReport extends ExcelBasicReport {
    @ExcelColumn("标的编码")
    private String symbol;

    //<editor-fold defaultstate="collapsed" desc="delombok">
    @SuppressWarnings("all")
    public ExcelGroupBasicReport() {
    }

    @SuppressWarnings("all")
    public String getSymbol() {
        return this.symbol;
    }

    @SuppressWarnings("all")
    public void setSymbol(final String symbol) {
        this.symbol = symbol;
    }

    @Override
    @SuppressWarnings("all")
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ExcelGroupBasicReport)) return false;
        final ExcelGroupBasicReport other = (ExcelGroupBasicReport) o;
        if (!other.canEqual((Object) this)) return false;
        if (!super.equals(o)) return false;
        final Object this$symbol = this.getSymbol();
        final Object other$symbol = other.getSymbol();
        if (this$symbol == null ? other$symbol != null : !this$symbol.equals(other$symbol)) return false;
        return true;
    }

    @SuppressWarnings("all")
    protected boolean canEqual(final Object other) {
        return other instanceof ExcelGroupBasicReport;
    }

    @Override
    @SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $symbol = this.getSymbol();
        result = result * PRIME + ($symbol == null ? 43 : $symbol.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings("all")
    public String toString() {
        return "ExcelGroupBasicReport(super=" + super.toString() + ", symbol=" + this.getSymbol() + ")";
    }
    //</editor-fold>
}
