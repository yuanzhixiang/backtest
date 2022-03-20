package com.yuanzhixiang.bt.strategy;

import java.util.List;

import com.yuanzhixiang.bt.domain.model.valobj.Factors;
import com.yuanzhixiang.bt.engine.Context;

/**
 * trade strategy
 *
 * @author yuanzhixiang
 */
public interface Strategy {

    /**
     * Notify next factors list
     *
     * @param context         the context is used to obtain the data used for the transaction
     * @param nextFactorsList next factors list
     */
    default void next(Context context, List<Factors> nextFactorsList) {
        for (Factors factors : nextFactorsList) {
            next(context, factors);
        }
    }

    /**
     * Notify next factors
     *
     * @param context the context is used to obtain the data used for the transaction
     * @param factors next factors
     */
    void next(Context context, Factors factors);

}
