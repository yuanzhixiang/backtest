package com.yuanzhixiang.bt.example.report.excel;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.yuanzhixiang.bt.example.report.excel.ExcelTable.Row;
import com.yuanzhixiang.bt.kit.MathUtil;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ReflectUtil;

/**
 * @author Yuan Zhixiang
 */
public class ExcelFactory {

    public static ExcelTable createTable(Object excelData, Map<String, Object> map) {
        ExcelTable excelTable = new ExcelTable();

        if (Collection.class.isAssignableFrom(excelData.getClass())) {
            for (Object next : (Collection<?>) excelData) {
                excelTable.putRow(createRow(next, null));
            }
        } else {
            excelTable.putRow(createRow(excelData, map));
        }

        return excelTable;
    }

    public static Row createRow(Object excelData, Map<String, Object> map) {
        Row row = new Row();
        if (map != null) {
            for (Entry<String, Object> entry : map.entrySet()) {
                row.putValue(entry.getKey(), entry.getValue());
            }
        }

        Field[] fields = ReflectUtil.getFields(excelData.getClass());
        for (Field field : fields) {
            ExcelColumn excelColumn = AnnotationUtil.getAnnotation(field, ExcelColumn.class);
            if (excelColumn == null) {
                continue;
            }

            Object fieldValue = ReflectUtil.getFieldValue(excelData, field);
            if (excelColumn.percentage() && fieldValue instanceof Number) {
                row.putValue(excelColumn.value(), MathUtil.formatPercent(((Number) fieldValue).doubleValue()));
            } else {
                row.putValue(excelColumn.value(), fieldValue);
            }
        }

        return row;
    }

    public static Row statisticRow(List excelData, Map<String, Object> map) {
        if (excelData.size() == 0) {
            return new Row();
        }

        Row row = new Row();
        if (map != null) {
            for (Entry<String, Object> entry : map.entrySet()) {
                row.putValue(entry.getKey(), entry.getValue());
            }
        }

        Map<String, BigDecimal> resultMap = new HashMap<>();
        Field[] fields = ReflectUtil.getFieldsDirectly(excelData.get(0).getClass(), true);

        for (Object excelDatum : excelData) {

            for (Field field : fields) {
                ExcelColumn excelColumn = AnnotationUtil.getAnnotation(field, ExcelColumn.class);
                if (excelColumn == null) {
                    continue;
                }

                BigDecimal value = resultMap.get(excelColumn.value());
                if (value == null) {
                    if (excelColumn.statisticType() == CalculateType.MIN) {
                        value = new BigDecimal(Integer.MAX_VALUE);
                    } else {
                        value = new BigDecimal(0);
                    }
                }

                Object fieldValue = ReflectUtil.getFieldValue(excelDatum, field);
                if (fieldValue instanceof Number) {
                    if (excelColumn.statisticType() == CalculateType.AVERAGE) {
                        value = value.add(BigDecimal.valueOf(((Number) fieldValue).doubleValue()));
                    } else if (excelColumn.statisticType() == CalculateType.MAX) {
                        value = BigDecimal.valueOf(Math.max(((Number) fieldValue).doubleValue(), value.doubleValue()));
                    } else if (excelColumn.statisticType() == CalculateType.MIN) {
                        value = BigDecimal.valueOf(Math.min(((Number) fieldValue).doubleValue(), value.doubleValue()));
                    }
                }

                resultMap.put(excelColumn.value(), value);
            }
        }

        for (Field field : fields) {
            ExcelColumn excelColumn = AnnotationUtil.getAnnotation(field, ExcelColumn.class);
            BigDecimal value = resultMap.get(excelColumn.value());
            if (excelColumn.statisticType() == CalculateType.AVERAGE) {
                value = value.divide(BigDecimal.valueOf(excelData.size()), 4, RoundingMode.HALF_UP);
            }

            if (value == null) {
                continue;
            }

            if (excelColumn.percentage()) {
                row.putValue(excelColumn.value(), MathUtil.formatPercent(value.doubleValue()));
            } else if (field.getType().equals(Double.class)) {
                row.putValue(excelColumn.value(), value.doubleValue());
            } else if (field.getType().equals(Integer.class)) {
                row.putValue(excelColumn.value(), value.intValue());
            }
        }

        return row;
    }

}
