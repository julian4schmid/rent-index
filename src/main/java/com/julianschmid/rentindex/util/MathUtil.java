package com.julianschmid.rentindex.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MathUtil {
    public static double calculateRelativeChange(double val) {
        return BigDecimal.valueOf(val - 1)
                .setScale(4, RoundingMode.FLOOR).doubleValue();
    }

    public static String formatPercentChange(double val) {
        double percent = calculateRelativeChange(val) * 100;
        return convertNumberToString(percent) + " %";
    }

    public static double roundWithDecimals(double val, int decimals) {
        return Math.round(val * Math.pow(10, decimals)) / Math.pow(10, decimals);
    }

    public static String convertNumberToString(double val) {
        return String.format("%.2f", val);
    }
}
