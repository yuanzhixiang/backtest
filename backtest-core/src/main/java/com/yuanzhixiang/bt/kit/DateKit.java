package com.yuanzhixiang.bt.kit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;

/**
 * @author yuanzhixiang
 */
public class DateKit {

    public static final LocalDateTime ZERO = LocalDateTime.of(1970, 1, 1, 8, 0);

    public static boolean between(LocalDateTime dateTime, LocalDateTime startDate, LocalDateTime endDate) {
        return (startDate == null || !dateTime.isBefore(startDate)) && (endDate == null || !dateTime.isAfter(endDate));
    }

    public static boolean between(LocalDateTime dateTime, LocalDate startDate, LocalDate endDate) {
        return (startDate == null || !dateTime.isBefore(startDate.atStartOfDay())) && (endDate == null || !dateTime.isAfter(endDate.plusDays(1).atStartOfDay()));
    }

    public static boolean notBetween(LocalDateTime dateTime, LocalDateTime startDate, LocalDateTime endDate) {
        return !between(dateTime, startDate, endDate);
    }

    public static boolean notBetween(LocalDateTime dateTime, LocalDate startDate, LocalDate endDate) {
        return !between(dateTime, startDate, endDate);
    }

    public static List<Month> getMonthDuration(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            return Collections.emptyList();
        }

        List<Month> monthDurationList = new ArrayList<>();
        int startYear = start.getYear();
        int endYear = end.getYear();
        while (startYear <= endYear) {
            int startMonth;
            if (startYear == start.getYear()) {
                startMonth = start.getMonthValue();
            } else {
                startMonth = 1;
            }

            int endMonth;
            if (startYear == end.getYear()) {
                endMonth = end.getMonthValue();
            } else {
                endMonth = 12;
            }

            while (startMonth <= endMonth) {
                monthDurationList.add(new Month(startYear, startMonth++));
            }

            startYear++;
        }

        return monthDurationList;
    }

    @Getter
    public static class Month {
        private final int year;
        private final int month;

        public Month(int year, int month) {
            this.year = year;
            this.month = month;
        }
    }

    public static LocalDateTime startOfMonth(int year, int month) {
        return LocalDateTime.of(year, month, 1, 0, 0);
    }

    public static LocalDateTime endOfMonth(int year, int month) {
        // The max month is 12
        if (month < 12) {
            return LocalDateTime.of(year, month + 1, 1, 23, 59, 59).minusDays(1);
        }
        return LocalDateTime.of(year + 1, 1, 1, 23, 59, 59).minusDays(1);
    }

    public static LocalDateTime startOfDay(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.of(0, 0, 0));
    }

    public static LocalDateTime endOfDay(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.of(23, 59, 59));
    }
}
