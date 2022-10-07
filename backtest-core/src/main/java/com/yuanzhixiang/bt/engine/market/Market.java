package com.yuanzhixiang.bt.engine.market;

import java.time.LocalDateTime;

import com.yuanzhixiang.bt.engine.domain.Factors;
import com.yuanzhixiang.bt.engine.domain.Symbol;

/**
 * @author Yuan Zhixiang
 */
public interface Market {

    /**
     * Query Factors
     *
     * @param symbol symbol
     * @param start  start date
     * @param end    end date
     * @return factors
     */
    CloseableIterator<Factors> queryFactors(Symbol symbol, LocalDateTime start, LocalDateTime end);
}
