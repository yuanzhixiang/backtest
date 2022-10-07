package com.yuanzhixiang.bt.engine.domain;

import com.yuanzhixiang.bt.engine.Local.LocalMap;
import com.yuanzhixiang.bt.engine.LocalVariable;

/**
 * @author Yuan Zhixiang
 */
public class Position implements LocalVariable {
    public Position(Symbol symbol, int quantity, int frozenQuantity, double buyInFee, double costPrice, LocalMap localMap) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.frozenQuantity = frozenQuantity;
        this.buyInFee = buyInFee;
        this.costPrice = costPrice;
        this.localMap = localMap;
    }

    private final Symbol symbol;
    private final int quantity;
    /**
     * todo 冻结数量的这个概念在去掉 T+1 交易之后是不是就可以去掉了
     */
    private final int frozenQuantity;
    private final double buyInFee;
    private final double costPrice;
    private LocalMap localMap;

    public int getTotalQuantity() {
        return quantity + frozenQuantity;
    }

    @Override
    public LocalMap getLocalMap() {
        return localMap;
    }

    @Override
    public void setLocalMap(LocalMap localMap) {
        this.localMap = localMap;
    }

    //<editor-fold defaultstate="collapsed" desc="delombok">
    @SuppressWarnings("all")
    public Symbol getSymbol() {
        return this.symbol;
    }

    @SuppressWarnings("all")
    public int getQuantity() {
        return this.quantity;
    }

    /**
     * todo 冻结数量的这个概念在去掉 T+1 交易之后是不是就可以去掉了
     */
    @SuppressWarnings("all")
    public int getFrozenQuantity() {
        return this.frozenQuantity;
    }

    @SuppressWarnings("all")
    public double getBuyInFee() {
        return this.buyInFee;
    }

    @SuppressWarnings("all")
    public double getCostPrice() {
        return this.costPrice;
    }

    @Override
    @SuppressWarnings("all")
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Position)) return false;
        final Position other = (Position) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getQuantity() != other.getQuantity()) return false;
        if (this.getFrozenQuantity() != other.getFrozenQuantity()) return false;
        if (Double.compare(this.getBuyInFee(), other.getBuyInFee()) != 0) return false;
        if (Double.compare(this.getCostPrice(), other.getCostPrice()) != 0) return false;
        final Object this$symbol = this.getSymbol();
        final Object other$symbol = other.getSymbol();
        if (this$symbol == null ? other$symbol != null : !this$symbol.equals(other$symbol)) return false;
        final Object this$localMap = this.getLocalMap();
        final Object other$localMap = other.getLocalMap();
        if (this$localMap == null ? other$localMap != null : !this$localMap.equals(other$localMap)) return false;
        return true;
    }

    @SuppressWarnings("all")
    protected boolean canEqual(final Object other) {
        return other instanceof Position;
    }

    @Override
    @SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getQuantity();
        result = result * PRIME + this.getFrozenQuantity();
        final long $buyInFee = Double.doubleToLongBits(this.getBuyInFee());
        result = result * PRIME + (int) ($buyInFee >>> 32 ^ $buyInFee);
        final long $costPrice = Double.doubleToLongBits(this.getCostPrice());
        result = result * PRIME + (int) ($costPrice >>> 32 ^ $costPrice);
        final Object $symbol = this.getSymbol();
        result = result * PRIME + ($symbol == null ? 43 : $symbol.hashCode());
        final Object $localMap = this.getLocalMap();
        result = result * PRIME + ($localMap == null ? 43 : $localMap.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings("all")
    public String toString() {
        return "Position(symbol=" + this.getSymbol() + ", quantity=" + this.getQuantity() + ", frozenQuantity=" + this.getFrozenQuantity() + ", buyInFee=" + this.getBuyInFee() + ", costPrice=" + this.getCostPrice() + ", localMap=" + this.getLocalMap() + ")";
    }
    //</editor-fold>
}
