package com.yuanzhixiang.bt.engine.domain;

import com.yuanzhixiang.bt.engine.Local.LocalMap;
import com.yuanzhixiang.bt.engine.LocalVariable;

import java.math.BigDecimal;

/**
 * @author Yuan Zhixiang
 */
public class Position implements LocalVariable {
    public Position(
            Symbol symbol,
            BigDecimal quantity,
            BigDecimal frozenQuantity,
            BigDecimal buyInFee,
            BigDecimal costPrice,
            LocalMap localMap
    ) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.frozenQuantity = frozenQuantity;
        this.buyInFee = buyInFee;
        this.costPrice = costPrice;
        this.localMap = localMap;
    }

    private final Symbol symbol;
    private final BigDecimal quantity;
    /**
     * todo 冻结数量的这个概念在去掉 T+1 交易之后是不是就可以去掉了
     */
    private final BigDecimal frozenQuantity;
    private final BigDecimal buyInFee;
    private final BigDecimal costPrice;
    private LocalMap localMap;

    public BigDecimal getTotalQuantity() {
        return quantity.add(frozenQuantity);
    }

    @Override
    public LocalMap getLocalMap() {
        return localMap;
    }

    @Override
    public void setLocalMap(LocalMap localMap) {
        this.localMap = localMap;
    }

    public Symbol getSymbol() {
        return this.symbol;
    }

    public BigDecimal getQuantity() {
        return this.quantity;
    }

    /**
     * todo 冻结数量的这个概念在去掉 T+1 交易之后是不是就可以去掉了
     */
    public BigDecimal getFrozenQuantity() {
        return this.frozenQuantity;
    }

    public BigDecimal getBuyInFee() {
        return this.buyInFee;
    }

    public BigDecimal getCostPrice() {
        return this.costPrice;
    }

}
