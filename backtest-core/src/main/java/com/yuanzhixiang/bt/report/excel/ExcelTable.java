package com.yuanzhixiang.bt.report.excel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yuanzhixiang
 */
public class ExcelTable {

    private List<Row> data = new ArrayList<>();

    public Row getRow() {
        if (data.size() == 0) {
            data.add(new Row());
        }

        return data.get(data.size() - 1);
    }

    public void putRow(Row row) {
        data.add(row);
    }

    public void putAllRow(List<Row> rows) {
        data.addAll(rows);
    }

    public void moveNextRow() {
        data.add(new Row());
    }

    public List<Map<String, Object>> getExcelData() {
        return data.stream().map(row -> row.dataMap).collect(Collectors.toList());
    }

    public static class Row {

        private Map<String, Object> dataMap = new LinkedHashMap<>();

        public void putValue(String key, Object value) {
            dataMap.put(key, value);
        }

        public <T> T get(String key) {
            return (T) dataMap.get(key);
        }

    }

}


