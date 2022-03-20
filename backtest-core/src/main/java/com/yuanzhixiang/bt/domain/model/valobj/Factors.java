package com.yuanzhixiang.bt.domain.model.valobj;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import com.yuanzhixiang.bt.engine.ContextLocal;
import com.yuanzhixiang.bt.engine.Local;
import com.yuanzhixiang.bt.engine.Local.LocalMap;
import com.yuanzhixiang.bt.engine.LocalVariable;

/**
 * @author yuanzhixiang
 */
public class Factors implements LocalVariable {

    private static final AtomicLong IDENTITY_TEMPLATE = new AtomicLong(0);

    private final Identity identity;

    public Factors(Symbol symbol, LocalDateTime tradeDate, Double open, Double close, Double high, Double low, Long volume, BigDecimal adjustment) {
        this(null, symbol, tradeDate, new Price(open, adjustment), new Price(close, adjustment), new Price(high, adjustment), new Price(low, adjustment), volume);
    }

    public Factors(Local<Symbol, List<Factors>> local, Symbol symbol, LocalDateTime tradeDate, Price open, Price close, Price high, Price low, Long volume) {
        identity = new Identity(local);
        this.symbol = symbol;
        this.tradeDate = tradeDate;
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.volume = volume;
    }

    private final Symbol symbol;
    /** trade date */
    private final LocalDateTime tradeDate;
    /** open price */
    private final Price open;
    /** close price */
    private final Price close;
    /** high price */
    private final Price high;
    /** low price */
    private final Price low;
    /** volume */
    private final Long volume;

    public Identity getIdentity() {
        return identity;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public LocalDateTime getTradeDate() {
        return tradeDate;
    }

    public Price getOpen() {
        return open;
    }

    public Price getClose() {
        return close;
    }

    public Price getHigh() {
        return high;
    }

    public Price getLow() {
        return low;
    }

    public Long getVolume() {
        return volume;
    }

    private LocalMap factorMap;

    @Override
    public LocalMap getLocalMap() {
        return factorMap;
    }

    @Override
    public void setLocalMap(LocalMap localMap) {
        this.factorMap = localMap;
    }

    public class Identity {

        private final long id = IDENTITY_TEMPLATE.getAndIncrement();

        /** If local is not null, it means it is the value in base factors local map. */
        private final Local<Symbol, List<Factors>> local;

        public Identity(Local<Symbol, List<Factors>> local) {
            this.local = local;
        }

        public Factors getFactors() {
            return Factors.this;
        }

        public Local<Symbol, List<Factors>> getLocal() {
            return local;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Identity identity = (Identity) o;
            return id == identity.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    /**
     * Traverse factors, the number of ranges forward from the current factors.
     *
     * @param cycle           cycle
     * @param factors         factors
     * @param factorsConsumer consumer
     */
    public static void foreach(int cycle, Factors factors, Consumer<Factors> factorsConsumer) {
        for (int offset = cycle * -1 + 1; offset <= 0; offset++) {
            factorsConsumer.accept(ContextLocal.get().getFactors(factors.getIdentity(), offset));
        }
    }
}
