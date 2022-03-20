package com.yuanzhixiang.bt.domain.repository;

import java.time.LocalDateTime;

import com.yuanzhixiang.bt.domain.model.valobj.Factors;
import com.yuanzhixiang.bt.domain.model.valobj.Symbol;

/**
 * @author yuanzhixiang
 */
public interface DataSource {

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
