package com.yuanzhixiang.bt.report;

import static cn.hutool.core.date.LocalDateTimeUtil.format;
import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.yuanzhixiang.bt.domain.model.entity.AccountEntity;
import com.yuanzhixiang.bt.domain.model.valobj.Factors;
import com.yuanzhixiang.bt.domain.model.valobj.OrderValObj;
import com.yuanzhixiang.bt.domain.model.valobj.Symbol;
import com.yuanzhixiang.bt.domain.model.valobj.position.Position;
import com.yuanzhixiang.bt.engine.Context;
import com.yuanzhixiang.bt.engine.SideEnum;
import com.yuanzhixiang.bt.report.excel.ExcelBasicReport;
import com.yuanzhixiang.bt.report.excel.ExcelDailyStatisticReport;
import com.yuanzhixiang.bt.report.excel.ExcelDeliveryOrder;

/**
 * @author yuanzhixiang
 */
public class Statistic {

    public Statistic(List<Symbol> symbolList) {
        this.symbolList = List.copyOf(symbolList);
        this.barEndStatisticList = new ArrayList<>();
    }

    private final List<Symbol> symbolList;

    public List<Symbol> getSymbolList() {
        return symbolList;
    }

    private final List<BarEndStatistic> barEndStatisticList;

    private List<OrderValObj> orderList;

    public void saveBarEndStatistic(Context context, LocalDateTime tradeDate, Symbol benchMark) {
        // Calculate assets
        AccountEntity account = context.getAccount();
        Map<Symbol, Position> allPosition = account.getPosition();
        BigDecimal sum = new BigDecimal(0);
        for (Position position : allPosition.values()) {
            Factors factors = context.getFactors(position.getSymbol(), 0);
            sum = sum.add(valueOf(position.getTotalQuantity()).multiply(valueOf(factors.getClose().getPrice())));
        }
        double asset = sum.add(valueOf(account.getBalance())).doubleValue();

        // Calculate bench mark price
        double benchMarkPrice = 0;
        if (benchMark != null) {
            Factors factors = context.getFactors(benchMark, 0);
            if (factors != null) {
                benchMarkPrice = factors.getClose().getPrice();
            }
        }

        // Save bar end statistic
        BarEndStatistic barEndStatistic = new BarEndStatistic(tradeDate, asset, 0, benchMarkPrice);
        barEndStatisticList.add(barEndStatistic);
    }

    public void saveOrderList(List<OrderValObj> orderList) {
        this.orderList = orderList;
    }

    public ExcelBasicReport statisticAll(double initializeAccountAsset) {
        ExcelBasicReport report = doStatistic(initializeAccountAsset, barEndStatisticList, orderList);

        BarEndStatistic latestStatistic = barEndStatisticList.get(barEndStatisticList.size() - 1);
        LocalDate startDate = barEndStatisticList.get(0).getDateTime().toLocalDate();
        LocalDate endDate = latestStatistic.getDateTime().toLocalDate();
        report.setRange(format(startDate, "yyyy/MM/dd") + "-" + format(endDate, "yyyy/MM/dd"));
        return report;
    }

    private ExcelBasicReport doStatistic(double initializeAccountAsset, List<BarEndStatistic> barEndStatisticList,
        List<OrderValObj> orderList) {
        double initializationAsset = barEndStatisticList.get(0).getAsset();
        barEndStatisticList = calculateDrawdown(barEndStatisticList);

        ExcelBasicReport report = new ExcelBasicReport();
        int tradeDay = tradeDay(barEndStatisticList);
        report.setTradeDay(tradeDay);

        int totalNumberOfTransactions = totalNumberOfTransactions(orderList);
        report.setTotalNumberOfTransactions(totalNumberOfTransactions);

        report.setAverageNumberOfTransactionsPerMonth(averageNumberOfTransactionsPerMonth(tradeDay, totalNumberOfTransactions));
        report.setAverageNumberOfTransactionsPerYear(averageNumberOfTransactionsPerYear(tradeDay, totalNumberOfTransactions));

        int numberOfVictories = numberOfVictories(orderList);
        report.setNumberOfVictories(numberOfVictories);
        report.setNumberOfFailures(totalNumberOfTransactions - numberOfVictories);

        report.setWinRate(winRate(totalNumberOfTransactions, numberOfVictories));
        report.setProfitLossRatio(profitLossRatio(orderList));

        BigDecimal income = income(initializationAsset, barEndStatisticList.get(barEndStatisticList.size() - 1).getAsset());

        report.setRelationInitializeBalanceRateOfReturn(RateOfReturn(income, initializeAccountAsset));
        report.setFinalRateOfReturn(RateOfReturn(income, initializationAsset));
        report.setAnnualizedRateOfReturn(annualizedRateOfReturn(report.getTradeDay(), income, initializationAsset));
        report.setMaximumRateOfReturn(maximumRateOfReturn(initializationAsset, maximumAsset(barEndStatisticList)));

        report.setSharpeRatio(statisticSharpeRatio(income, barEndStatisticList));

        report.setMaximumDrawdownRate(maximumDrawdownRate(barEndStatisticList));
        report.setMaximumDrawdownTime(maximumDrawdownTime(barEndStatisticList));

        return report;
    }

    private int tradeDay(List<BarEndStatistic> barEndStatisticList) {
        int tradeDay = 0;
        LocalDate localDate = null;
        for (BarEndStatistic barEndStatistic : barEndStatisticList) {
            LocalDate barLocalDate = barEndStatistic.getDateTime().toLocalDate();
            if (!barLocalDate.equals(localDate)) {
                localDate = barLocalDate;
                tradeDay++;
            }
        }
        return tradeDay;
    }

    private Double averageNumberOfTransactionsPerMonth(int tradeDay, int totalNumberOfTransactions) {
        return valueOf(totalNumberOfTransactions).divide(valueOf(tradeDay), 4, RoundingMode.HALF_UP)
            .multiply(valueOf(21)).doubleValue();
    }

    private Double averageNumberOfTransactionsPerYear(int tradeDay, int totalNumberOfTransactions) {
        return valueOf(totalNumberOfTransactions).divide(valueOf(tradeDay), 4, RoundingMode.HALF_UP)
            .multiply(valueOf(250)).doubleValue();
    }

    private static List<BarEndStatistic> calculateDrawdown(List<BarEndStatistic> barEndStatisticList) {
        ArrayList<BarEndStatistic> barEndStatistics = new ArrayList<>(barEndStatisticList.size());
        double maximumAsset = 0;
        for (BarEndStatistic barEndStatistic : barEndStatisticList) {
            maximumAsset = Math.max(maximumAsset, barEndStatistic.getAsset());
            double drawdown = valueOf(maximumAsset).subtract(valueOf(barEndStatistic.getAsset()))
                .divide(valueOf(maximumAsset), 4, RoundingMode.HALF_UP).doubleValue();
            barEndStatistics.add(new BarEndStatistic(barEndStatistic.getDateTime(), barEndStatistic.getAsset(),
                drawdown, barEndStatistic.getBenchmarkPrice()));
        }

        return barEndStatistics;
    }

    private static double maximumAsset(List<BarEndStatistic> barEndStatisticList) {
        double maximumAsset = 0;
        for (BarEndStatistic barEndStatistic : barEndStatisticList) {
            maximumAsset = Math.max(maximumAsset, barEndStatistic.getAsset());
        }
        return maximumAsset;
    }

    private static int totalNumberOfTransactions(List<OrderValObj> orderList) {
        return (int) orderList.stream().filter(order -> SideEnum.SELL.name().equals(order.getSide())).count();
    }

    private static int numberOfVictories(List<OrderValObj> orderList) {
        return (int) orderList.stream().filter(order -> order.getProfit() >= 0)
            .filter(order -> SideEnum.SELL.name().equals(order.getSide())).count();
    }

    private static double winRate(int totalNumberOfTransactions, int numberOfVictories) {
        if (numberOfVictories != 0) {
            return valueOf(numberOfVictories)
                .divide(valueOf(totalNumberOfTransactions), 2, RoundingMode.HALF_UP)
                .doubleValue();
        } else {
            return 0;
        }
    }

    private Double profitLossRatio(List<OrderValObj> orderList) {

        BigDecimal sumProfit = valueOf(0);
        BigDecimal sumLoss = valueOf(0);
        int countProfit = 0;
        int countLoss = 0;

        for (OrderValObj order : orderList) {
            if (order.getProfit() > 0) {
                sumProfit = sumProfit.add(valueOf(order.getProfit()));
                countProfit++;
            }

            if (order.getProfit() < 0) {
                sumLoss = sumLoss.add(valueOf(order.getProfit()));
                countLoss++;
            }
        }

        double averageProfit = countProfit == 0 ? 0 : sumProfit.divide(valueOf(countProfit), 2, RoundingMode.HALF_UP).doubleValue();
        double averageLoss = countLoss == 0 ? 0 : sumLoss.divide(valueOf(countLoss), 2, RoundingMode.HALF_UP).doubleValue();

        if (averageLoss == 0) {
            return Double.MAX_VALUE;
        }
        return valueOf(averageProfit).divide(valueOf(averageLoss * -1), 2, RoundingMode.HALF_UP).doubleValue();
    }

    private static BigDecimal income(double initializationAsset, double finalAsset) {
        return valueOf(finalAsset).subtract(valueOf(initializationAsset));
    }

    private static double RateOfReturn(BigDecimal income, double initializationAsset) {
        return income.divide(valueOf(initializationAsset), 4, RoundingMode.HALF_UP).doubleValue();
    }

    private static double annualizedRateOfReturn(int tradeDuration, BigDecimal income, double initializationAsset) {
        return income
            // Money earned every day
            .divide(valueOf(tradeDuration), 8, RoundingMode.HALF_UP)
            // Daily rate of return
            .divide(valueOf(initializationAsset), 8, RoundingMode.HALF_UP)
            // Annualized rate of return
            .multiply(valueOf(365)).setScale(2, RoundingMode.HALF_UP)
            // Get result
            .doubleValue();
    }

    private static double maximumRateOfReturn(double initializationAsset, double maximumAsset) {
        return valueOf(maximumAsset).subtract(valueOf(initializationAsset))
            .divide(valueOf(initializationAsset), 4, RoundingMode.HALF_UP).doubleValue();
    }

    private static double statisticSharpeRatio(BigDecimal income, List<BarEndStatistic> barEndStatisticList) {
        if (income.doubleValue() == 0) {
            return 0;
        }

        // Calculate average value
        BigDecimal averageValue = new BigDecimal(0);
        for (BarEndStatistic barEndStatistic : barEndStatisticList) {
            averageValue = averageValue.add(valueOf(barEndStatistic.getAsset()));
        }
        averageValue = averageValue.divide(valueOf(barEndStatisticList.size()), 4, RoundingMode.HALF_UP);

        // Calculate standard deviation
        BigDecimal standardDeviation = new BigDecimal(0);
        for (BarEndStatistic barEndStatistic : barEndStatisticList) {
            BigDecimal difference = valueOf(barEndStatistic.getAsset()).subtract(averageValue);
            standardDeviation = standardDeviation.add(difference.multiply(difference));
        }
        double standardDeviationValue = standardDeviation
            .divide(valueOf(barEndStatisticList.size()), 4, RoundingMode.HALF_UP).doubleValue();
        standardDeviationValue = Math.sqrt(standardDeviationValue);

        // Calculate sharpe ratio
        return income.divide(valueOf(standardDeviationValue), 4, RoundingMode.HALF_UP).doubleValue();
    }

    private static double maximumDrawdownRate(List<BarEndStatistic> barEndStatisticList) {
        double maximumDrawdown = 0;
        for (BarEndStatistic barEndStatistic : barEndStatisticList) {
            if (maximumDrawdown - barEndStatistic.getDrawdown() < 0) {
                maximumDrawdown = barEndStatistic.getDrawdown();
            }
        }
        return maximumDrawdown;
    }

    private static int maximumDrawdownTime(List<BarEndStatistic> barEndStatisticList) {
        int maximumDrawdownTime = 0;
        int drawdownTime = 0;
        LocalDate localDate = null;
        Iterator<BarEndStatistic> iterator = barEndStatisticList.iterator();
        while (iterator.hasNext()) {
            BarEndStatistic barEndStatistic = iterator.next();
            LocalDate barLocalDate = barEndStatistic.getDateTime().toLocalDate();

            if (localDate == null || barLocalDate.equals(localDate)) {
                localDate = barLocalDate;
                continue;
            } else {
                localDate = barLocalDate;
            }

            // Snapshot drawdown
            if (barEndStatistic.getDrawdown() == 0) {
                if (maximumDrawdownTime < drawdownTime) {
                    maximumDrawdownTime = drawdownTime;
                }
                drawdownTime = 0;
            }
            // Add drawdown time
            else {
                drawdownTime++;
            }

            // The last element
            if (!iterator.hasNext()) {
                if (maximumDrawdownTime < drawdownTime) {
                    maximumDrawdownTime = drawdownTime;
                }
            }
        }

        return maximumDrawdownTime;
    }

    public List<ExcelBasicReport> statisticByMonthly(double initializeAccountAsset) {
        List<ExcelBasicReport> reportList = new ArrayList<>();
        List<BarEndStatistic> monthlyBarEndStatistic = new ArrayList<>(30);
        BarEndStatistic previousBarEndStatistic = null;
        Iterator<BarEndStatistic> iterator = barEndStatisticList.iterator();
        while (iterator.hasNext()) {
            BarEndStatistic next = iterator.next();

            if (previousBarEndStatistic != null
                && previousBarEndStatistic.getDateTime().getMonthValue() != next.getDateTime().getMonthValue()) {
                doStatisticByMonthly(initializeAccountAsset, reportList, monthlyBarEndStatistic);

                monthlyBarEndStatistic = new ArrayList<>(30);
            }

            monthlyBarEndStatistic.add(next);
            previousBarEndStatistic = next;

            if (!iterator.hasNext() && monthlyBarEndStatistic.size() != 0) {
                doStatisticByMonthly(initializeAccountAsset, reportList, monthlyBarEndStatistic);
            }
        }

        return reportList;
    }

    private void doStatisticByMonthly(double initializeAccountAsset, List<ExcelBasicReport> reportList,
        List<BarEndStatistic> monthlyBarEndStatistic) {
        LocalDate startDate = monthlyBarEndStatistic.get(0).getDateTime().toLocalDate();
        LocalDate endDate = monthlyBarEndStatistic.get(monthlyBarEndStatistic.size() - 1).getDateTime().toLocalDate();
        ExcelBasicReport report = doStatistic(initializeAccountAsset, monthlyBarEndStatistic, sliceOrderList(startDate, endDate, orderList));
        report.setRange(format(startDate, "yyyy/MM"));
        reportList.add(report);
    }

    private static List<OrderValObj> sliceOrderList(LocalDate startDate, LocalDate endDate, List<OrderValObj> orderList) {
        return orderList.stream().filter(orderValObj -> {
            LocalDate localDate = orderValObj.getTradeDate().toLocalDate();
            return localDate.isEqual(startDate) || localDate.isEqual(endDate)
                || (localDate.isAfter(startDate) && localDate.isBefore(endDate));
        }).collect(Collectors.toList());
    }

    public List<ExcelDailyStatisticReport> getAssetChangesSheet() {
        return barEndStatisticList.stream().map(element -> {
            ExcelDailyStatisticReport analysisReport = new ExcelDailyStatisticReport();
            analysisReport.setTradeDate(element.getDateTime());

            analysisReport.setAsset(element.getAsset());
            analysisReport.setBenchmark(element.getBenchmarkPrice());
            analysisReport.setDrawdown(element.getDrawdown());
            return analysisReport;
        }).collect(Collectors.toList());
    }

    public List<ExcelDeliveryOrder> getOrder() {
        return orderList.stream().map(order -> {
            ExcelDeliveryOrder excelDeliveryOrder = new ExcelDeliveryOrder();
            excelDeliveryOrder.setTradeDate(order.getTradeDate());
            excelDeliveryOrder.setSymbol(order.getSymbol());
            excelDeliveryOrder.setSide(order.getSide());
            excelDeliveryOrder.setQuantity(order.getQuantity());
            excelDeliveryOrder.setPrice(order.getPrice());
            excelDeliveryOrder.setAmount(order.getAmount());
            excelDeliveryOrder.setProfit(order.getProfit());
            excelDeliveryOrder.setTransferFee(order.getTransferFee());
            excelDeliveryOrder.setCommissionFee(order.getCommissionFee());
            excelDeliveryOrder.setStampDuty(order.getStampDuty());
            excelDeliveryOrder.setRemark(order.getRemark());
            return excelDeliveryOrder;
        }).collect(Collectors.toList());
    }
}
