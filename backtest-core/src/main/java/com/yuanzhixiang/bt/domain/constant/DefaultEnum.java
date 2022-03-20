package com.yuanzhixiang.bt.domain.constant;

/**
 * @author yuanzhixiang
 */
public interface DefaultEnum {

    String name();

    default String getCode() {
        return name();
    }

}
