package com.yuanzhixiang.bt.factor.variant;

import com.yuanzhixiang.bt.service.ContextService;
import com.yuanzhixiang.bt.engine.domain.Factors;

/**
 * @author Yuan Zhixiang
 */
public interface VariantFactor {

    Factors bind(ContextService contextService, Factors factors);

}
