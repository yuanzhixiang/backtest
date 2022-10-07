package com.yuanzhixiang.bt.engine;

import java.util.List;

import com.yuanzhixiang.bt.engine.domain.Factors;

/**
 * @author Yuan Zhixiang
 */
public interface LifeCycle {

    default void initialize(Context contextImpl) {
    }

    default void strategyNext(Context contextImpl, List<Factors> nextFactorsList) {
    }

    default void strategyEnd(Context contextImpl) {
    }

}
