package com.yuanzhixiang.bt.report;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import org.ttzero.excel.entity.Workbook;

import com.yuanzhixiang.bt.domain.model.valobj.Factors;
import com.yuanzhixiang.bt.engine.Configuration;
import com.yuanzhixiang.bt.engine.Context;
import com.yuanzhixiang.bt.engine.Local;
import com.yuanzhixiang.bt.factor.variant.PeriodFactorDaily;
import com.yuanzhixiang.bt.report.excel.ExcelBasicReport;
import com.yuanzhixiang.bt.report.excel.ExcelFactory;

import cn.hutool.core.date.LocalDateTimeUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yuanzhixiang
 */
@Slf4j
public class DefaultReporter implements Reporter {

    protected Path path;

    protected static final Local<Context, Statistic> STATISTIC_LOCAL = new Local<>();

    public DefaultReporter() {
        path = Paths.get(System.getProperty("user.home") + File.separator + "Desktop" + File.separator +
            LocalDateTimeUtil.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public void initialize(Context context) {
        STATISTIC_LOCAL.set(context, new Statistic(context.getTradeSymbolList()));
    }

    @Override
    public void strategyNext(Context context, List<Factors> nextFactorsList) {
        Configuration configuration = context.getConfiguration();

        if (PeriodFactorDaily.change(nextFactorsList.get(0))) {
            Statistic statistic = STATISTIC_LOCAL.get(context);
            statistic.saveBarEndStatistic(context, nextFactorsList.get(0).getTradeDate(), configuration.getBenchmark());
        }
    }

    @Override
    public void strategyEnd(Context context) {
        Statistic statistic = STATISTIC_LOCAL.get(context);
        Configuration configuration = context.getConfiguration();

        // Save order list
        statistic.saveOrderList(context.getSecuritiesExchange().getOrderList());

        // Generate report
        List<ExcelBasicReport> excelBasicReports = statistic.statisticByMonthly(configuration.getAccountBalance());
        ExcelBasicReport report = statistic.statisticAll(configuration.getAccountBalance());
        excelBasicReports.add(0, report);

        generateReport(path.toString(), statistic, excelBasicReports);
    }

    protected void generateReport(String pathStr, Statistic statistic, List<ExcelBasicReport> excelBasicReports) {
        try {
            Path path = Paths.get(pathStr);
            new Workbook(path.getFileName().toString())
                .addSheet("基本信息", ExcelFactory.createTable(excelBasicReports, null).getExcelData())
                .addSheet("资产统计", ExcelFactory.createTable(statistic.getAssetChangesSheet(), null).getExcelData())
                .addSheet("交割单", ExcelFactory.createTable(statistic.getOrder(), null).getExcelData())
                .writeTo(path.getParent());
        } catch (IOException e) {
            log.error("", e);
        } finally {
            log.info("Generate analysis report to {}.", pathStr);
        }
    }

}
