package com.yuanzhixiang.bt.engine;

import java.util.List;

import com.yuanzhixiang.bt.domain.model.valobj.Factors;

/**
 * @author yuanzhixiang
 */
public interface LifeCycle {

    default void initialize(Context context) {
    }

    default void strategyNext(Context context, List<Factors> nextFactorsList) {
    }

    default void strategyEnd(Context context) {
    }

}
