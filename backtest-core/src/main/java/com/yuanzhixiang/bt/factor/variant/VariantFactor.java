package com.yuanzhixiang.bt.factor.variant;

import com.yuanzhixiang.bt.engine.Context;
import com.yuanzhixiang.bt.domain.model.valobj.Factors;

/**
 * @author yuanzhixiang
 */
public interface VariantFactor {

    Factors bind(Context context, Factors factors);

}
