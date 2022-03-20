package com.yuanzhixiang.bt.kit;

import static com.yuanzhixiang.bt.kit.DateKit.endOfMonth;
import static com.yuanzhixiang.bt.kit.DateKit.getMonthDuration;
import static com.yuanzhixiang.bt.kit.DateKit.startOfMonth;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.yuanzhixiang.bt.kit.DateKit.Month;

import junit.framework.TestCase;

/**
 * @author yuanzhixiang
 * @date 2022/01/11 15:00:25
 */
public class DateKitTest extends TestCase {

    public void testGetMonthDuration() {
        List<Month> monthDurationList = getMonthDuration(
            LocalDateTime.of(2020, 11, 1, 0, 0),
            LocalDateTime.of(2021, 2, 1, 0, 0)
        );

        List<String> assetList = Arrays.asList("2020-11", "2020-12", "2021-1", "2021-2");

        assertEquals(assetList.size(), monthDurationList.size());
        for (int i = 0; i < assetList.size(); i++) {
            Month month = monthDurationList.get(i);
            assertEquals(assetList.get(i), month.getYear() + "-" + month.getMonth());
        }
    }

    public void testStartOfMonth() {
        assertEquals(LocalDateTime.of(2012, 1, 1, 0, 0), startOfMonth(2012, 1));
    }

    public void testEndOfMonth() {
        assertEquals(LocalDateTime.of(2022, 1, 31, 23, 59, 59), endOfMonth(2022, 1));
        assertEquals(LocalDateTime.of(2022, 2, 28, 23, 59, 59), endOfMonth(2022, 2));
        assertEquals(LocalDateTime.of(2022, 3, 31, 23, 59, 59), endOfMonth(2022, 3));
        assertEquals(LocalDateTime.of(2022, 4, 30, 23, 59, 59), endOfMonth(2022, 4));
        assertEquals(LocalDateTime.of(2022, 5, 31, 23, 59, 59), endOfMonth(2022, 5));
        assertEquals(LocalDateTime.of(2022, 6, 30, 23, 59, 59), endOfMonth(2022, 6));
        assertEquals(LocalDateTime.of(2022, 12, 31, 23, 59, 59), endOfMonth(2022, 12));

        assertEquals(LocalDateTime.of(2023, 1, 31, 23, 59, 59), endOfMonth(2023, 1));
        assertEquals(LocalDateTime.of(2023, 2, 28, 23, 59, 59), endOfMonth(2023, 2));
    }
}
