package com.yuanzhixiang.bt.factor.common;

import com.yuanzhixiang.bt.engine.Configuration;
import com.yuanzhixiang.bt.engine.Context;
import com.yuanzhixiang.bt.domain.model.valobj.Factors;

/**
 * @author yuanzhixiang
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
     * @param context context
     * @param factors factor sets
     */
    void bind(Context context, Factors factors);

}
