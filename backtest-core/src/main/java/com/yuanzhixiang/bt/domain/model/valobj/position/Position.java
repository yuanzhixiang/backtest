package com.yuanzhixiang.bt.domain.model.valobj.position;

import com.yuanzhixiang.bt.domain.model.valobj.Symbol;
import com.yuanzhixiang.bt.engine.Local.LocalMap;
import com.yuanzhixiang.bt.engine.LocalVariable;

import lombok.Data;

/**
 * @author yuanzhixiang
 */
@Data
public class Position implements LocalVariable {

    public Position(Symbol symbol, int quantity, int frozenQuantity, double buyInFee, double costPrice,
        LocalMap localMap) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.frozenQuantity = frozenQuantity;
        this.buyInFee = buyInFee;
        this.costPrice = costPrice;
        this.localMap = localMap;
    }

    private final Symbol symbol;

    private final int quantity;

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
}
