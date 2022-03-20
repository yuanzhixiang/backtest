package com.yuanzhixiang.bt.engine;

import java.util.Collection;
import java.util.List;

import com.yuanzhixiang.bt.domain.model.valobj.Factors;

/**
 * @author yuanzhixiang
 */
public class RunListeners implements LifeCycle {

    public RunListeners(Collection<LifeCycle> lifeCycles) {
        this.lifeCycles = lifeCycles;
    }

    private final Collection<LifeCycle> lifeCycles;

    @Override
    public void initialize(Context context) {
        for (LifeCycle lifeCycle : lifeCycles) {
            lifeCycle.initialize(context);
        }
    }

    @Override
    public void strategyNext(Context context, List<Factors> nextFactorsList) {
        for (LifeCycle lifeCycle : lifeCycles) {
            lifeCycle.strategyNext(context, nextFactorsList);
        }
    }

    @Override
    public void strategyEnd(Context context) {
        for (LifeCycle lifeCycle : lifeCycles) {
            lifeCycle.strategyEnd(context);
        }
    }
}
