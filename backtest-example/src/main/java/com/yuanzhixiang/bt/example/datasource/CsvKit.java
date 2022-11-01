package com.yuanzhixiang.bt.example.datasource;

import static cn.hutool.core.text.csv.CsvUtil.getWriter;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.yuanzhixiang.bt.engine.domain.Factors;
import com.yuanzhixiang.bt.engine.domain.Symbol;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.csv.CsvWriter;

/**
 * @author Yuan Zhixiang
 */
public class CsvKit {
    //<editor-fold defaultstate="collapsed" desc="delombok">
    @SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CsvKit.class);
    //</editor-fold>

    public static void write(String path, List<Factors> factorsList) {
        if (CollectionUtil.isEmpty(factorsList)) {
            return;
        }
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Factors factors : factorsList) {
            HashMap<String, Object> map = new LinkedHashMap<>(16);
            map.put("date", LocalDateTimeUtil.format(factors.getTradeDate(), "yyyyMMddmmHHss"));
            map.put("high", factors.getHigh().getPrice());
            map.put("low", factors.getLow().getPrice());
            map.put("close", factors.getClose().getPrice());
            map.put("open", factors.getOpen().getPrice());
            map.put("volume", factors.getVolume());
            map.put("adjustment", factors.getOpen().getAdjustment());
            dataList.add(map);
        }
        File cacheFile = new File(path);
        if (cacheFile.exists() && !cacheFile.delete()) {
            log.error("Delete cache file fail, file path is {}.", cacheFile.getAbsoluteFile());
        }
        try (CsvWriter writer = getWriter(path, StandardCharsets.UTF_8)) {
            String[] keys;
            if (dataList.size() < 10) {
                keys = dataList.get(0).keySet().toArray(new String[0]);
            } else {
                keys = dataList.get(dataList.size() - 3).keySet().toArray(new String[0]);
            }
            List<String> poList = dataList.stream().map(data -> {
                StringBuilder value = new StringBuilder();
                for (int i = 0; i < keys.length; i++) {
                    Object datum = data.get(keys[i]);
                    if (datum != null) {
                        value.append(datum);
                    }
                    if (i + 1 < keys.length) {
                        value.append(',');
                    }
                }
                return value.toString();
            }).collect(Collectors.toList());
            poList.add(0, String.join(",", keys));
            writer.write(poList);
        }
    }

    public static List<Factors> read(String code, String path) {
        Symbol symbol = new Symbol(code);
        List<Factors> response = new LinkedList<>();
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        MappingIterator<Map<String, String>> iterator;
        try {
            iterator = mapper.readerFor(Map.class).with(schema).readValues(new File(path));
            while (iterator.hasNext()) {
                Map<String, String> next = iterator.next();
                BigDecimal close = BigDecimal.valueOf(Double.parseDouble(next.get("close")));
                BigDecimal open = BigDecimal.valueOf(Double.parseDouble(next.get("open")));
                BigDecimal high = BigDecimal.valueOf(Double.parseDouble(next.get("high")));
                BigDecimal low = BigDecimal.valueOf(Double.parseDouble(next.get("low")));
                BigDecimal adjustment = BigDecimal.valueOf(Double.parseDouble(next.get("adjustment")));
                BigDecimal volume = BigDecimal.valueOf(Long.parseLong(next.get("volume")));
                LocalDateTime date = LocalDateTimeUtil.parse(next.get("date"), "yyyyMMddmmHHss");
                response.add(new Factors(symbol, date, open, close, high, low, volume, adjustment));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return response;
    }
}
