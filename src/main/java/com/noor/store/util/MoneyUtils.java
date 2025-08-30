package com.noor.store.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MoneyUtils {
    public static BigDecimal scale(BigDecimal v) {
        if (v == null) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return v.setScale(2, RoundingMode.HALF_UP);
    }
}
