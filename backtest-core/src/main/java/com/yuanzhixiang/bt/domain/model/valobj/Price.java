package com.yuanzhixiang.bt.domain.model.valobj;

import java.math.BigDecimal;

import com.yuanzhixiang.bt.engine.Local.LocalMap;
import com.yuanzhixiang.bt.engine.LocalVariable;

/**
 * @author yuanzhixiang
 */
public class Price implements LocalVariable {

    private final Double price;

    private final BigDecimal adjustment;

    public Price(Double price, BigDecimal adjustment) {
        this.price = price;
        this.adjustment = adjustment;
    }

    public Double getPrice() {
        return price;
    }

    public BigDecimal getAdjustment() {
        return adjustment;
    }

    private LocalMap localMap;

    @Override
    public LocalMap getLocalMap() {
        return localMap;
    }

    @Override
    public void setLocalMap(LocalMap localMap) {
        this.localMap = localMap;
    }
}
