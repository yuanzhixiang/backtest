package com.yuanzhixiang.bt.domain.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.yuanzhixiang.bt.engine.*;
import com.yuanzhixiang.bt.engine.market.CloseableIterator;
import com.yuanzhixiang.bt.engine.market.Market;
import com.yuanzhixiang.bt.service.CounterService;
import org.junit.Assert;
import org.junit.Test;

import com.yuanzhixiang.bt.engine.domain.Factors;
import com.yuanzhixiang.bt.engine.domain.Symbol;
import com.yuanzhixiang.bt.engine.Strategy;

/**
 * @author Yuan Zhixiang
 */
public class IteratorWrapperTest {

    @Test
    public void testBackMethod() {
        Configuration configuration = new Configuration();
        configuration.setDataSource(new TestBackMethodDS());
        configuration.setCounter(new CounterService(0, 0, 0, 0, false));
        configuration.setTimeRange(LocalDateTime.of(2001, 1, 1, 0, 0),
            LocalDateTime.of(2001, 2, 1, 0, 0));
        configuration.setSymbolList(List.of(
            new Symbol("000001"),
            new Symbol("000002")
        ));
        new Launcher(configuration).start("", false, new Strategy() {

            private int batch = 1;

            @Override
            public void next(Context context, List<Factors> nextFactorsList) {
                if (batch == 1) {
                    Assert.assertEquals(nextFactorsList.size(), 2);
                    Assert.assertEquals(nextFactorsList.get(0).getTradeDate().getDayOfMonth(), 2);
                    Assert.assertEquals(nextFactorsList.get(1).getTradeDate().getDayOfMonth(), 2);
                } else if (batch == 2) {
                    Assert.assertEquals(nextFactorsList.size(), 1);
                    Assert.assertEquals(nextFactorsList.get(0).getTradeDate().getDayOfMonth(), 3);
                } else if (batch == 3) {
                    Assert.assertEquals(nextFactorsList.size(), 1);
                    Assert.assertEquals(nextFactorsList.get(0).getTradeDate().getDayOfMonth(), 4);
                } else if (batch == 4) {
                    Assert.assertEquals(nextFactorsList.size(), 2);
                    Assert.assertEquals(nextFactorsList.get(0).getTradeDate().getDayOfMonth(), 5);
                    Assert.assertEquals(nextFactorsList.get(1).getTradeDate().getDayOfMonth(), 5);
                } else if (batch == 5) {
                    Assert.assertEquals(nextFactorsList.size(), 1);
                    Assert.assertEquals(nextFactorsList.get(0).getTradeDate().getDayOfMonth(), 6);
                } else if (batch == 6) {
                    Assert.assertEquals(nextFactorsList.size(), 1);
                    Assert.assertEquals(nextFactorsList.get(0).getTradeDate().getDayOfMonth(), 7);
                } else if (batch == 7) {
                    Assert.assertEquals(nextFactorsList.size(), 2);
                    Assert.assertEquals(nextFactorsList.get(0).getTradeDate().getDayOfMonth(), 8);
                    Assert.assertEquals(nextFactorsList.get(1).getTradeDate().getDayOfMonth(), 8);
                }

                batch++;
            }

            @Override
            public void next(Context context, Factors factors) {
                throw new UnsupportedOperationException();
            }
        });
    }

    private static class TestBackMethodDS implements Market {

        private static boolean firstNew = true;

        @Override
        public CloseableIterator<Factors> queryFactors(Symbol symbol, LocalDateTime start, LocalDateTime end) {
            List<Factors> factorsList = new ArrayList<>();

            factorsList.add(new Factors(symbol, LocalDateTime.of(2001, 1, 2, 0, 0), 0d, 0d, 0d, 0d, 0L, BigDecimal.ZERO));
            if (firstNew) {
                factorsList.add(new Factors(symbol, LocalDateTime.of(2001, 1, 3, 0, 0), 0d, 0d, 0d, 0d, 0L, BigDecimal.ZERO));
            } else {
                factorsList.add(new Factors(symbol, LocalDateTime.of(2001, 1, 4, 0, 0), 0d, 0d, 0d, 0d, 0L, BigDecimal.ZERO));
            }
            factorsList.add(new Factors(symbol, LocalDateTime.of(2001, 1, 5, 0, 0), 0d, 0d, 0d, 0d, 0L, BigDecimal.ZERO));

            if (firstNew) {
                factorsList.add(new Factors(symbol, LocalDateTime.of(2001, 1, 7, 0, 0), 0d, 0d, 0d, 0d, 0L, BigDecimal.ZERO));
            } else {
                factorsList.add(new Factors(symbol, LocalDateTime.of(2001, 1, 6, 0, 0), 0d, 0d, 0d, 0d, 0L, BigDecimal.ZERO));
            }

            factorsList.add(new Factors(symbol, LocalDateTime.of(2001, 1, 8, 0, 0), 0d, 0d, 0d, 0d, 0L, BigDecimal.ZERO));

            firstNew = false;

            return new CloseableIterator<>() {
                private final Iterator<Factors> iterator = factorsList.iterator();

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public Factors next() {
                    return iterator.next();
                }
            };
        }
    }
}
