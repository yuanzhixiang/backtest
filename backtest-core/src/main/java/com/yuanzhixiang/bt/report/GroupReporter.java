package com.yuanzhixiang.bt.report;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.ttzero.excel.entity.Workbook;

import com.yuanzhixiang.bt.domain.model.valobj.Symbol;
import com.yuanzhixiang.bt.engine.Configuration;
import com.yuanzhixiang.bt.engine.Context;
import com.yuanzhixiang.bt.report.excel.ExcelBasicReport;
import com.yuanzhixiang.bt.report.excel.ExcelFactory;
import com.yuanzhixiang.bt.report.excel.ExcelTable;
import com.yuanzhixiang.bt.report.excel.ExcelTable.Row;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yuanzhixiang
 */
@Slf4j
public class GroupReporter extends DefaultReporter {

    protected final Map<Symbol, ExcelBasicReport> reportMap = new ConcurrentHashMap<>();

    private final boolean generateSymbolReport;

    public GroupReporter(boolean generateSymbolReport) {
        this.generateSymbolReport = generateSymbolReport;
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
        Symbol symbol = statistic.getSymbolList().get(0);
        if (generateSymbolReport) {
            Path parent = path.getParent();

            File folder = new File(parent + File.separator + path.getFileName());
            if (!folder.exists() && !folder.mkdirs()) {
                log.error("Create report folder fail, path: {}.", folder.getAbsolutePath());
            }

            generateReport(folder.getAbsolutePath() + File.separator + symbol.getCode(), statistic, excelBasicReports);
        }

        reportMap.put(symbol, report);
    }

    public void generate() {
        ExcelTable excelTable = new ExcelTable();
        Map<String, Object> header = new HashMap<>(2);
        header.put("标的编码", "平均值");
        Row row = ExcelFactory.statisticRow(Arrays.asList(reportMap.values().toArray()), header);
        excelTable.putRow(row);

        List<Row> rows = reportMap.entrySet().stream()
            .map(entry -> {
                Map<String, Object> map = new HashMap<>(2);
                map.put("标的编码", entry.getKey());
                return ExcelFactory.createRow(entry.getValue(), map);
            })
            .collect(Collectors.toList());

        excelTable.putAllRow(rows);

        try {
            new Workbook(path.getFileName().toString())
                .addSheet("基本信息", excelTable.getExcelData())
                .writeTo(path.getParent());
        } catch (IOException e) {
            log.error("", e);
        }
    }
}
