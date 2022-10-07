package com.yuanzhixiang.bt.factor.common;

import com.yuanzhixiang.bt.engine.Configuration;
import com.yuanzhixiang.bt.service.ContextService;
import com.yuanzhixiang.bt.engine.domain.Factors;

/**
 * @author Yuan Zhixiang
 */
public interface Factor<F extends Factor<F>> {

    /**
     * This method is called when a factor is registered.
     *
     * @param configuration configuration
     * @param old           a previously registered factor of the same type, then the old will be abandoned
     */
    default void register(Configuration configuration, F old) {
        // Do nothing.
    }

    /**
     * Bind factor to factor sets.
     *
     * @param contextService context
     * @param factors factor sets
     */
    void bind(ContextService contextService, Factors factors);

}
