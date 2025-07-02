package com.julianschmid.rentindex.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MathUtil {
    public static double calculateRelativeChange(double val) {
        return BigDecimal.valueOf(val - 1)
                .setScale(4, RoundingMode.FLOOR).doubleValue();
    }

    public static String formatPercentChange(double val) {
        return calculateRelativeChange(val) * 100 + " %";
    }
}
