package com.julianschmid.rentindex.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MathUtil {
    public static double calculatePercentChange(double val) {
        return BigDecimal.valueOf((val - 1) * 100)
                .setScale(2, RoundingMode.FLOOR).doubleValue();
    }

    public static String formatPercentChange(double val) {
        return convertNumberToString(val).replace(".", ",") + " %";
    }

    public static double roundWithDecimals(double val, int decimals) {
        return Math.round(val * Math.pow(10, decimals)) / Math.pow(10, decimals);
    }

    public static String convertNumberToString(double val) {
        return String.format("%.2f", val);
    }
}
