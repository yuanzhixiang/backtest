package com.yuanzhixiang.bt.engine;

import java.util.Collection;
import java.util.List;

import com.yuanzhixiang.bt.engine.domain.Factors;

/**
 * @author Yuan Zhixiang
 */
public class RunListeners implements LifeCycle {

    public RunListeners(Collection<LifeCycle> lifeCycles) {
        this.lifeCycles = lifeCycles;
    }

    private final Collection<LifeCycle> lifeCycles;

    @Override
    public void initialize(Context contextImpl) {
        for (LifeCycle lifeCycle : lifeCycles) {
            lifeCycle.initialize(contextImpl);
        }
    }

    @Override
    public void strategyNext(Context contextImpl, List<Factors> nextFactorsList) {
        for (LifeCycle lifeCycle : lifeCycles) {
            lifeCycle.strategyNext(contextImpl, nextFactorsList);
        }
    }

    @Override
    public void strategyEnd(Context contextImpl) {
        for (LifeCycle lifeCycle : lifeCycles) {
            lifeCycle.strategyEnd(contextImpl);
        }
    }
}
