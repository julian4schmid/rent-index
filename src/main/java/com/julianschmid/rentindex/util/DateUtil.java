package com.julianschmid.rentindex.util;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public final class DateUtil {

    private DateUtil() {
    }

    public static final List<String> MONTH_ORDER = Arrays.asList(
            "Januar", "Februar", "März", "April", "Mai", "Juni",
            "Juli", "August", "September", "Oktober", "November", "Dezember"
    );

    public static String getDateToday() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return today.format(formatter);
    }

    public static int getCurrentYear() {
        return Year.now().getValue();
    }

    public static String getMonth(int additionalMonths) {
        int currentMonth = LocalDate.now().getMonthValue() - 1;
        return MONTH_ORDER.get((currentMonth + additionalMonths) % 12);
    }

    public static String getMonth() {
        return getMonth(0);
    }

    public static int getYear(int additionalMonths) {
        int currentMonth = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();
        if (currentMonth + additionalMonths > 12) {
            year += 1;
        }
        return year;
    }
}