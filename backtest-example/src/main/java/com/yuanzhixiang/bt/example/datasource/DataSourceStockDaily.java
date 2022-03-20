package com.yuanzhixiang.bt.example.datasource;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

import com.yuanzhixiang.bt.domain.model.valobj.Factors;
import com.yuanzhixiang.bt.domain.model.valobj.Symbol;
import com.yuanzhixiang.bt.domain.repository.DataSource;
import com.yuanzhixiang.bt.domain.repository.CloseableIterator;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yuanzhixiang
 */
@Slf4j
public class DataSourceStockDaily implements DataSource {

    @Override
    public CloseableIterator<Factors> queryFactors(Symbol symbol, LocalDateTime start, LocalDateTime end) {
        return new IteratorImpl();
    }

}

class IteratorImpl implements CloseableIterator<Factors> {

    public IteratorImpl() {
        List<Factors> factors = CsvKit.read("000001", DataSourceStockDaily.class.getResource("/000001.csv").getPath());
        iterator = factors.iterator();
    }

    private final Iterator<Factors> iterator;

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Factors next() {
        return iterator.next();
    }
}
