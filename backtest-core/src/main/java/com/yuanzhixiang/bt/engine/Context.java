package com.yuanzhixiang.bt.engine;

import com.yuanzhixiang.bt.engine.domain.Factors;
import com.yuanzhixiang.bt.engine.domain.Symbol;

/**
 * @author Yuan Zhixiang
 */
public interface Context extends LocalVariable {

    Counter getCounter();

    // todo getFactors 提取到行情中
    Factors getFactors(Symbol symbol, int offset);

    Factors getFactors(Factors.Identity identity, int offset);
}
