package com.yuanzhixiang.bt.domain.model.aggregate;

import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.yuanzhixiang.bt.domain.model.entity.AccountEntity;
import com.yuanzhixiang.bt.domain.model.valobj.Factors;
import com.yuanzhixiang.bt.domain.model.valobj.Factors.Identity;
import com.yuanzhixiang.bt.domain.model.valobj.OrderValObj;
import com.yuanzhixiang.bt.domain.model.valobj.Symbol;
import com.yuanzhixiang.bt.domain.model.valobj.position.Position;
import com.yuanzhixiang.bt.domain.model.valobj.position.PositionRealAdjustment;
import com.yuanzhixiang.bt.domain.repository.IteratorWrapper;
import com.yuanzhixiang.bt.engine.Configuration;
import com.yuanzhixiang.bt.engine.Local;
import com.yuanzhixiang.bt.engine.SideEnum;
import com.yuanzhixiang.bt.exception.BackTestException;
import com.yuanzhixiang.bt.factor.variant.PeriodFactorDaily;
import com.yuanzhixiang.bt.domain.repository.CloseableIterator;
import com.yuanzhixiang.bt.kit.SymbolUtil;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yuanzhixiang
 */
@Slf4j
public class SecuritiesExchangeAggregate {

    public SecuritiesExchangeAggregate(Configuration configuration, double stampDutyRate, double transferFeeRate) {
        if (stampDutyRate < 0 || transferFeeRate < 0 || configuration.getCommissionRate() < 0) {
            throw new BackTestException("StampDutyRate, TransferFeeRate, CommissionRate must be great than 0.");
        }
        this.configuration = configuration;
        this.stampDutyRate = stampDutyRate;
        this.transferFeeRate = valueOf(transferFeeRate);
        this.commissionRate = valueOf(configuration.getCommissionRate());
    }

    private Configuration configuration;

    @Getter
    private AccountEntity accountEntity;

    private final double stampDutyRate;

    private final BigDecimal transferFeeRate;

    private final BigDecimal commissionRate;

    // todo If the delivery order records are stored in the memory, then a memory leak will be caused when the number
    //   of delivery orders is very large.
    @Getter
    private final List<OrderValObj> orderList = new ArrayList<>();

    public void registerAccount(AccountEntity accountEntity) {
        this.accountEntity = accountEntity;
    }

    /**
     * Place a buy order
     *
     * @param symbol     symbol code
     * @param tradeTime  trade time
     * @param price      buy price
     * @param percentage percentage of position, range in (0, 1]
     */
    public synchronized Position buy(Symbol symbol, LocalDateTime tradeTime, double price, double percentage, String remark) {
        if (percentage > 1 || percentage <= 0) {
            throw new BackTestException("Percentage must be between (0, 1]");
        }

        // Calculate the quantity that can be bought
        double balance = accountEntity.getBalance();
        int quantity = valueOf(balance).multiply(valueOf(percentage)).divide(valueOf(price), 0, RoundingMode.DOWN)
            .intValue();
        quantity = (quantity / 100) * 100;

        return buySpecifiedQuantity(symbol, tradeTime, price, quantity, remark);
    }

    /**
     * Place a buy order
     *
     * @param symbol    symbol code
     * @param tradeTime trade time
     * @param price     buy price
     * @param quantity  buy in quantity
     */
    public synchronized Position buySpecifiedQuantity(Symbol symbol, LocalDateTime tradeTime, double price,
        int quantity, String remark) {
        if (quantity <= 0) {
            return null;
        }

        BigDecimal balance = valueOf(accountEntity.getBalance());

        // calculate exchange cost
        BigDecimal buyQuantity = valueOf(quantity);
        BigDecimal amount = valueOf(price).multiply(buyQuantity);
        BigDecimal transferFee = getTransferFee(symbol, buyQuantity);
        BigDecimal commissionFee = getCommissionRate(amount);
        BigDecimal deduct = amount.add(transferFee).add(commissionFee);

        // Calculate
        BigDecimal balanceAfterPurchase = balance.subtract(deduct);
        if (balanceAfterPurchase.doubleValue() < 0) {
            return null;
            // When the amount of funds is relatively large, there will be a recursive stack overflow problem.
//            return buySpecifiedQuantity(symbol, tradeTime, price, quantity - 100);
        }

        // Increase position
        Position position = accountEntity.getPosition(symbol);
        BigDecimal oldHoldQuantity = valueOf(position.getQuantity());
        BigDecimal oldFrozenQuantity = valueOf(position.getFrozenQuantity());
        BigDecimal oldBuyInFee = valueOf(position.getBuyInFee());
        BigDecimal newBuyInFee = oldBuyInFee.add(deduct);
        BigDecimal newHoldQuantity = configuration.isTodayAddOneTrade() ? oldHoldQuantity : oldHoldQuantity.add(buyQuantity);
        BigDecimal newFrozenQuantity = configuration.isTodayAddOneTrade() ?  oldFrozenQuantity.add(buyQuantity) : oldFrozenQuantity;
        BigDecimal newCostPrice = newBuyInFee.divide(newHoldQuantity.add(newFrozenQuantity), 2, RoundingMode.HALF_UP);

        accountEntity.setBalance(balance.subtract(deduct).doubleValue());
        accountEntity.putPosition(new Position(symbol, newHoldQuantity.intValue(), newFrozenQuantity.intValue(),
            newBuyInFee.doubleValue(), newCostPrice.doubleValue(), position.getLocalMap()));

        orderList.add(new OrderValObj(tradeTime, symbol, SideEnum.BUY.name(), quantity, price,
            deduct.doubleValue(), transferFee.doubleValue(), commissionFee.doubleValue(), 0, 0, remark));

        return position;
    }

    /**
     * Place a sell order
     *
     * @param symbol     symbol code
     * @param tradeTime  trade time
     * @param price      sell price
     * @param percentage percentage of position, range in (0, 1]
     */
    public synchronized void sell(Symbol symbol, LocalDateTime tradeTime, double price, double percentage, String remark) {
        if (percentage > 1 || percentage <= 0) {
            throw new BackTestException("Percentage must be between (0, 1]");
        }

        Position position = accountEntity.getPosition(symbol);
        if (position.getQuantity() == 0) {
            return;
        }

        int quantity = valueOf(position.getQuantity()).multiply(valueOf(percentage)).intValue();
        quantity = Math.min(quantity, position.getQuantity());

        sellSpecifiedQuantity(symbol, tradeTime, price, quantity, remark);
    }

    public synchronized void sellSpecifiedQuantity(Symbol symbol, LocalDateTime tradeTime, double price, int quantity,
        String remark) {
        if (quantity <= 0) {
            return;
        }

        Position position = accountEntity.getPosition(symbol);

        BigDecimal sellQuantity = valueOf(quantity);
        // calculate exchange cost
        BigDecimal sellAmount = valueOf(price).multiply(sellQuantity);
        BigDecimal transferFee = getTransferFee(symbol, sellQuantity);
        BigDecimal commissionFee = getCommissionRate(sellAmount);
        BigDecimal stampDuty = getStampDuty(sellAmount);

        // Expense deductions
        sellAmount = sellAmount.subtract(transferFee).subtract(commissionFee).subtract(stampDuty);
        BigDecimal oldHoldQuantity = valueOf(position.getQuantity());
        BigDecimal oldFrozenQuantity = valueOf(position.getFrozenQuantity());
        BigDecimal oldTotalQuantity = oldHoldQuantity.add(oldFrozenQuantity);
        BigDecimal oldBuyInFee = valueOf(position.getBuyInFee());

        BigDecimal newHoldQuantity = oldHoldQuantity.subtract(sellQuantity);
        BigDecimal newTotalQuantity = newHoldQuantity.add(oldFrozenQuantity);
        BigDecimal purchaseCost = oldBuyInFee.multiply(sellQuantity).divide(oldTotalQuantity, 2, RoundingMode.HALF_UP);
        BigDecimal profit = sellAmount.subtract(purchaseCost);
        BigDecimal newBuyInFee = oldBuyInFee.subtract(purchaseCost);

        BigDecimal newCostPrice = newTotalQuantity.intValue() == 0
            ? BigDecimal.ZERO : newBuyInFee.divide(newTotalQuantity, 2, RoundingMode.HALF_UP);

        accountEntity.setBalance(valueOf(accountEntity.getBalance()).add(sellAmount).doubleValue());
        accountEntity.putPosition(new Position(symbol, newHoldQuantity.intValue(), position.getFrozenQuantity(),
            newBuyInFee.doubleValue(), newCostPrice.doubleValue(), position.getLocalMap()));

        orderList.add(new OrderValObj(tradeTime, symbol, SideEnum.SELL.name(), quantity, price,
            sellAmount.doubleValue(), transferFee.doubleValue(), commissionFee.doubleValue(),
            stampDuty.doubleValue(), profit.doubleValue(), remark));
    }

    public BigDecimal getTransferFee(Symbol symbol, BigDecimal quantity) {
        if (transferFeeRate.doubleValue() != 0 && SymbolUtil.isSSE(symbol.getCode())) {
            return quantity.multiply(transferFeeRate).setScale(2, RoundingMode.HALF_UP);
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getCommissionRate(BigDecimal amount) {
        if (commissionRate.doubleValue() != 0) {
            return amount.multiply(commissionRate).setScale(2, RoundingMode.HALF_UP);
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getStampDuty(BigDecimal amount) {
        return valueOf(stampDutyRate).multiply(amount).setScale(2, RoundingMode.HALF_UP);
    }

    // For bar iterator

    @Getter
    private final BarIterator barIterator = new BarIterator();

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

    public class BarIterator implements Iterator<List<Factors>> {

        private class Pair {

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
                    }
                    // If the time of the latter factor is more advanced, the previous factor will be rolled.
                    else if (previousPair.next.getTradeDate().isAfter(pair.next.getTradeDate())) {
                        for (Pair stalePair : nextPairList) {
                            stalePair.iterator.back();
                        }
                        nextPairList = new LinkedList<>();
                        nextPairList.add(pair);
                    }
                    // Roll back the next factor if the time of the previous factor is earlier.
                    else {
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
            boolean mergeFrozenQuantity = configuration.isTodayAddOneTrade() && PeriodFactorDaily.change(factorsList.get(0));

            // Adjustment position
            for (Position position : accountEntity.getPosition().values()) {

                if (position.getTotalQuantity() == 0) {
                    // Empty localMap if there is no quantity
                    if (position.getLocalMap() != null) {
                        position.setLocalMap(null);
                    }
                    continue;
                }

                BigDecimal realAdjustment = PositionRealAdjustment.get(position);
                BigDecimal newHoldQuantity = valueOf(position.getQuantity()).multiply(realAdjustment);
                BigDecimal newFrozenQuantity = valueOf(position.getFrozenQuantity()).multiply(realAdjustment);

                if (mergeFrozenQuantity) {
                    newHoldQuantity = newHoldQuantity.add(newFrozenQuantity);
                    newFrozenQuantity = BigDecimal.ZERO;
                }

                BigDecimal newBuyInFee = valueOf(position.getBuyInFee()).multiply(realAdjustment);
                BigDecimal newCostPrice = valueOf(position.getCostPrice()).multiply(realAdjustment);

                accountEntity.putPosition(new Position(position.getSymbol(), newHoldQuantity.intValue(),
                    newFrozenQuantity.intValue(), newBuyInFee.doubleValue(), newCostPrice.doubleValue(),
                    position.getLocalMap()));
            }

            return factorsList;
        }

        public Factors getFactors(Identity identity, int offset) {
            if (offset == 0) {
                return identity.getFactors();
            }

            if (offset > 0) {
                return null;
            }

            Local<Symbol, List<Factors>> periodListLocal = identity.getLocal() == null
                ? DEFAULT_PERIOD : identity.getLocal();
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
















