package com.yuanzhixiang.bt.engine;

import com.yuanzhixiang.bt.engine.domain.Factors;
import com.yuanzhixiang.bt.engine.domain.Factors.Identity;
import com.yuanzhixiang.bt.engine.domain.Symbol;
import com.yuanzhixiang.bt.engine.market.CloseableIterator;
import com.yuanzhixiang.bt.engine.market.IteratorWrapper;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * @author Yuan Zhixiang
 */
public class ExchangeAggregate {
    //<editor-fold defaultstate="collapsed" desc="delombok">
    @SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ExchangeAggregate.class);
    //</editor-fold>

    public ExchangeAggregate(Configuration configuration, Counter counter) {
        this.counter = counter;
        barIterator = new BarIterator(configuration, symbolList, counter);
    }

    private final Counter counter;

    public Counter getCounter() {
        return counter;
    }

    // For bar iterator
    private final BarIterator barIterator;

    public BarIterator getBarIterator() {
        return barIterator;
    }

    private static final Local<Symbol, IteratorWrapper<Factors>> DEFAULT_PERIOD_ITERATOR = new Local<>();
    private static final Local<Symbol, List<Factors>> DEFAULT_PERIOD = new Local<>();
    private final List<Symbol> symbolList = new ArrayList<>();

    public void initializeQuotation(Map<Symbol, IteratorWrapper<Factors>> dataMap) {
        for (Entry<Symbol, IteratorWrapper<Factors>> entry : dataMap.entrySet()) {
            symbolList.add(entry.getKey());
            DEFAULT_PERIOD.set(entry.getKey(), new ArrayList<>());
            DEFAULT_PERIOD_ITERATOR.set(entry.getKey(), entry.getValue());
        }
    }


    //<editor-fold defaultstate="collapsed" desc="delombok">
    public static class BarIterator implements Iterator<List<Factors>> {
    //</editor-fold>
        private final List<Symbol> symbolList;
        private final Configuration configuration;
        private final Counter counter;

        public BarIterator(Configuration configuration, List<Symbol> symbolList, Counter counter) {
            this.symbolList = symbolList;
            this.configuration = configuration;
            this.counter = counter;
        }


        //<editor-fold defaultstate="collapsed" desc="delombok">
        private class Pair {
        //</editor-fold>
            private Symbol symbol;
            private Factors next;
            private IteratorWrapper<Factors> iterator;
        }

        @Override
        public boolean hasNext() {
            for (Symbol symbol : symbolList) {
                CloseableIterator<Factors> iterator = DEFAULT_PERIOD_ITERATOR.get(symbol);
                if (iterator.hasNext()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public List<Factors> next() {
            // Collection next factors
            LinkedList<Pair> nextPairList = new LinkedList<>();
            for (Symbol symbol : symbolList) {
                IteratorWrapper<Factors> iterator = DEFAULT_PERIOD_ITERATOR.get(symbol);
                if (!iterator.hasNext()) {
                    continue;
                }
                Pair pair = new Pair();
                pair.iterator = iterator;
                pair.next = iterator.next();
                pair.symbol = symbol;
                if (nextPairList.size() == 0) {
                    nextPairList.add(pair);
                } else {
                    Pair previousPair = nextPairList.get(0);
                    // Add it to the factor list at the same time.
                    if (previousPair.next.getTradeDate().equals(pair.next.getTradeDate())) {
                        nextPairList.add(pair);
                    } else 
                    // If the time of the latter factor is more advanced, the previous factor will be rolled.
                    if (previousPair.next.getTradeDate().isAfter(pair.next.getTradeDate())) {
                        for (Pair stalePair : nextPairList) {
                            stalePair.iterator.back();
                        }
                        nextPairList = new LinkedList<>();
                        nextPairList.add(pair);
                    } else 
                    // Roll back the next factor if the time of the previous factor is earlier.
                    {
                        pair.iterator.back();
                    }
                }
            }
            for (Pair pair : nextPairList) {
                List<Factors> factorsList = DEFAULT_PERIOD.get(pair.symbol);
                factorsList.add(pair.next);
                if (factorsList.size() > 300) {
                    factorsList.remove(0);
                }
            }
            List<Factors> factorsList = nextPairList.stream().map(pair -> pair.next).collect(Collectors.toList());
            // Adjustment position
            counter.refresh();
            return factorsList;
        }

        public Factors getFactors(Identity identity, int offset) {
            if (offset == 0) {
                return identity.getFactors();
            }
            if (offset > 0) {
                return null;
            }
            Local<Symbol, List<Factors>> periodListLocal = identity.getLocal() == null ? DEFAULT_PERIOD : identity.getLocal();
            List<Factors> factorsList = periodListLocal.get(identity.getFactors().getSymbol());
            if (factorsList == null) {
                return null;
            }
            int anchorIndex = factorsList.size() - 1;
            for (; anchorIndex >= 0; anchorIndex--) {
                Factors factors = factorsList.get(anchorIndex);
                if (factors.getIdentity().equals(identity)) {
                    break;
                }
            }
            int index = anchorIndex + offset;
            if (index < 0 || index >= factorsList.size()) {
                return null;
            }
            return factorsList.get(index);
        }

        public Factors getFactors(Symbol symbol, int offset) {
            if (offset > 0) {
                return null;
            }
            List<Factors> factorsList = DEFAULT_PERIOD.get(symbol);
            if (factorsList == null) {
                return null;
            }
            int index = factorsList.size() - 1 + offset;
            if (index < 0 || index >= factorsList.size()) {
                return null;
            }
            return factorsList.get(index);
        }
    }
}
