package com.yuanzhixiang.bt.engine;

import com.yuanzhixiang.bt.exception.BackTestException;

import lombok.Getter;

/**
 * @author yuanzhixiang
 */
public enum SideEnum {
    BUY(0), SELL(1);

    SideEnum(int code) {
        this.code = code;
    }

    @Getter
    private int code;

    public static SideEnum parseSide(String side) {
        if ("0".equals(side)) {
            return BUY;
        }

        if ("1".equals(side)) {
            return SELL;
        }

        throw new BackTestException("Not excepted side, side: [" + side + "]");
    }
}
