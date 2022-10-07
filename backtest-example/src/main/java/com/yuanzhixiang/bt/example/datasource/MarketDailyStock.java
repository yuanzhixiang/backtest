package com.yuanzhixiang.bt.example.datasource;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import com.yuanzhixiang.bt.engine.domain.Factors;
import com.yuanzhixiang.bt.engine.domain.Symbol;
import com.yuanzhixiang.bt.engine.market.Market;
import com.yuanzhixiang.bt.engine.market.CloseableIterator;

/**
 * @author Yuan Zhixiang
 */
public class MarketDailyStock implements Market {
    @Override
    public CloseableIterator<Factors> queryFactors(Symbol symbol, LocalDateTime start, LocalDateTime end) {
        return new IteratorImpl();
    }
}

class IteratorImpl implements CloseableIterator<Factors> {
    public IteratorImpl() {
        List<Factors> factors = CsvKit.read("000001", MarketDailyStock.class.getResource("/000001.csv").getPath());
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
