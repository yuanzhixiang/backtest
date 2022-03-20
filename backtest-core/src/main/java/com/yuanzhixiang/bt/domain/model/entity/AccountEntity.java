package com.yuanzhixiang.bt.domain.model.entity;

import java.util.HashMap;
import java.util.Map;

import com.yuanzhixiang.bt.domain.model.valobj.Symbol;
import com.yuanzhixiang.bt.domain.model.valobj.position.Position;

import lombok.Getter;
import lombok.Setter;

/**
 * @author yuanzhixiang
 */
public class AccountEntity {

    @Getter
    @Setter
    private double balance;

    private final Map<Symbol, Position> positionMap = new HashMap<>();

    public AccountEntity() {
    }

    /**
     * Update position
     *
     * @param position position
     */
    public void putPosition(Position position) {
        positionMap.put(position.getSymbol(), position);
    }

    /**
     * Get position
     *
     * @param symbol symbol
     * @return position
     */
    public Position getPosition(Symbol symbol) {
        Position position = positionMap.get(symbol);
        if (position == null) {
            position = new Position(symbol, 0, 0, 0, 0, null);
            positionMap.put(symbol, position);
        }
        return position;
    }

    /**
     * Get all position
     *
     * @return all position
     */
    public Map<Symbol, Position> getPosition() {
        return new HashMap<>(positionMap);
    }
}
