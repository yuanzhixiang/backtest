package com.yuanzhixiang.bt.example.report;

import static cn.hutool.core.date.LocalDateTimeUtil.format;
import static java.math.BigDecimal.valueOf;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.yuanzhixiang.bt.engine.domain.Factors;
import com.yuanzhixiang.bt.engine.domain.Symbol;
import com.yuanzhixiang.bt.engine.domain.Position;
import com.yuanzhixiang.bt.engine.Context;
import com.yuanzhixiang.bt.engine.Counter;
import com.yuanzhixiang.bt.engine.SideEnum;
import com.yuanzhixiang.bt.engine.domain.TradeRecord;
import com.yuanzhixiang.bt.example.report.excel.ExcelBasicReport;
import com.yuanzhixiang.bt.example.report.excel.ExcelDailyStatisticReport;
import com.yuanzhixiang.bt.example.report.excel.ExcelDeliveryOrder;

/**
 * @author Yuan Zhixiang
 */
public class Statistic {

    public Statistic() {
        this.barEndStatisticList = new ArrayList<>();
    }

    private final List<BarEndStatistic> barEndStatisticList;

    private List<TradeRecord> tradeRecords;

    public void saveBarEndStatistic(Context context, List<Symbol> symbols, LocalDateTime tradeDate, Symbol benchMark) {
        // Calculate assets
        Counter counter = context.getCounter();
        BigDecimal sum = new BigDecimal(0);
        for (Symbol symbol : symbols) {
            Position position = counter.queryPosition(symbol);
            Factors factors = context.getFactors(symbol, 0);
            sum = sum.add(position.getTotalQuantity().multiply(factors.getClose().getPrice()));
        }
        double asset = sum.add(counter.queryBalance()).doubleValue();

        // Calculate bench mark price
        double benchMarkPrice = 0;
        if (benchMark != null) {
            Factors factors = context.getFactors(benchMark, 0);
            if (factors != null) {
                benchMarkPrice = factors.getClose().getPrice().doubleValue();
            }
        }

        // Save bar end statistic
        BarEndStatistic barEndStatistic = new BarEndStatistic(tradeDate, asset, 0, benchMarkPrice);
        barEndStatisticList.add(barEndStatistic);
    }

    public void saveTradeRecordList(List<TradeRecord> orderList) {
        this.tradeRecords = orderList;
    }

    public ExcelBasicReport statisticAll(double initializeAccountAsset) {
        ExcelBasicReport report = doStatistic(initializeAccountAsset, barEndStatisticList, tradeRecords);

        BarEndStatistic latestStatistic = barEndStatisticList.get(barEndStatisticList.size() - 1);
        LocalDate startDate = barEndStatisticList.get(0).getDateTime().toLocalDate();
        LocalDate endDate = latestStatistic.getDateTime().toLocalDate();
        report.setRange(format(startDate, "yyyy/MM/dd") + "-" + format(endDate, "yyyy/MM/dd"));
        return report;
    }

    private ExcelBasicReport doStatistic(double initializeAccountAsset, List<BarEndStatistic> barEndStatisticList,
        List<TradeRecord> tradeRecords) {
        double initializationAsset = barEndStatisticList.get(0).getAsset();
        barEndStatisticList = calculateDrawdown(barEndStatisticList);

        ExcelBasicReport report = new ExcelBasicReport();
        int tradeDay = tradeDay(barEndStatisticList);
        report.setTradeDay(tradeDay);

        int totalNumberOfTransactions = totalNumberOfTransactions(tradeRecords);
        report.setTotalNumberOfTransactions(totalNumberOfTransactions);

        report.setAverageNumberOfTransactionsPerMonth(averageNumberOfTransactionsPerMonth(tradeDay, totalNumberOfTransactions));
        report.setAverageNumberOfTransactionsPerYear(averageNumberOfTransactionsPerYear(tradeDay, totalNumberOfTransactions));

        int numberOfVictories = numberOfVictories(tradeRecords);
        report.setNumberOfVictories(numberOfVictories);
        report.setNumberOfFailures(totalNumberOfTransactions - numberOfVictories);

        report.setWinRate(winRate(totalNumberOfTransactions, numberOfVictories));
        report.setProfitLossRatio(profitLossRatio(tradeRecords));

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

    private static int totalNumberOfTransactions(List<TradeRecord> tradeRecords) {
        return (int) tradeRecords.stream().filter(order -> SideEnum.SELL.equals(order.getSide())).count();
    }

    private static int numberOfVictories(List<TradeRecord> tradeRecords) {
        return (int) tradeRecords.stream().filter(order -> order.getProfit() >= 0)
            .filter(order -> SideEnum.SELL.equals(order.getSide())).count();
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

    private Double profitLossRatio(List<TradeRecord> tradeRecords) {

        BigDecimal sumProfit = valueOf(0);
        BigDecimal sumLoss = valueOf(0);
        int countProfit = 0;
        int countLoss = 0;

        for (TradeRecord tradeRecord : tradeRecords) {
            if (tradeRecord.getProfit() > 0) {
                sumProfit = sumProfit.add(valueOf(tradeRecord.getProfit()));
                countProfit++;
            }

            if (tradeRecord.getProfit() < 0) {
                sumLoss = sumLoss.add(valueOf(tradeRecord.getProfit()));
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

    public List<ExcelBasicReport> statisticByMonthly(double initializeBalance) {
        List<ExcelBasicReport> reportList = new ArrayList<>();
        List<BarEndStatistic> monthlyBarEndStatistic = new ArrayList<>(30);
        BarEndStatistic previousBarEndStatistic = null;
        Iterator<BarEndStatistic> iterator = barEndStatisticList.iterator();
        while (iterator.hasNext()) {
            BarEndStatistic next = iterator.next();

            if (previousBarEndStatistic != null
                && previousBarEndStatistic.getDateTime().getMonthValue() != next.getDateTime().getMonthValue()) {
                doStatisticByMonthly(initializeBalance, reportList, monthlyBarEndStatistic);

                monthlyBarEndStatistic = new ArrayList<>(30);
            }

            monthlyBarEndStatistic.add(next);
            previousBarEndStatistic = next;

            if (!iterator.hasNext() && monthlyBarEndStatistic.size() != 0) {
                doStatisticByMonthly(initializeBalance, reportList, monthlyBarEndStatistic);
            }
        }

        return reportList;
    }

    private void doStatisticByMonthly(double initializeAccountAsset, List<ExcelBasicReport> reportList,
        List<BarEndStatistic> monthlyBarEndStatistic) {
        LocalDate startDate = monthlyBarEndStatistic.get(0).getDateTime().toLocalDate();
        LocalDate endDate = monthlyBarEndStatistic.get(monthlyBarEndStatistic.size() - 1).getDateTime().toLocalDate();
        ExcelBasicReport report = doStatistic(initializeAccountAsset, monthlyBarEndStatistic, sliceOrderList(startDate, endDate, tradeRecords));
        report.setRange(format(startDate, "yyyy/MM"));
        reportList.add(report);
    }

    private static List<TradeRecord> sliceOrderList(LocalDate startDate, LocalDate endDate, List<TradeRecord> tradeRecords) {
        return tradeRecords.stream().filter(order -> {
            LocalDate localDate = order.getTradeDate().toLocalDate();
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
        return tradeRecords.stream().map(order -> {
            ExcelDeliveryOrder excelDeliveryOrder = new ExcelDeliveryOrder();
            excelDeliveryOrder.setTradeDate(order.getTradeDate());
            excelDeliveryOrder.setSymbol(order.getSymbol());
            excelDeliveryOrder.setSide(order.getSide().name());
            excelDeliveryOrder.setQuantity(order.getQuantity());
            excelDeliveryOrder.setPrice(order.getPrice());
            excelDeliveryOrder.setAmount(order.getAmount());
            excelDeliveryOrder.setProfit(order.getProfit());
            excelDeliveryOrder.setTransferFee(order.getTransferFee());
            excelDeliveryOrder.setCommissionFee(order.getCommissionFee());
            excelDeliveryOrder.setStampDuty(order.getStampDuty());
            return excelDeliveryOrder;
        }).collect(Collectors.toList());
    }
}
