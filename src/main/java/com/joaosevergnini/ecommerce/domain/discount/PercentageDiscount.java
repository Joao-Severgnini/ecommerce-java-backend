package com.joaosevergnini.ecommerce.domain.discount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class PercentageDiscount implements Discount{
    private final BigDecimal percentage;

    public PercentageDiscount(BigDecimal percentage) {
        Objects.requireNonNull(percentage, "percentage");
        if (percentage.signum() < 0 || percentage.compareTo(BigDecimal.ONE) > 0)
            throw new IllegalArgumentException("percentage must be between 0 and 1");
        this.percentage = percentage;
    }

    @Override
    public BigDecimal apply(BigDecimal originalValue){
        Objects.requireNonNull(originalValue, "originalValue");
        return originalValue.subtract(originalValue.multiply(percentage)).setScale(2, RoundingMode.HALF_UP);
    }

    public String getType() {
        return "PERCENTAGE";
    }

    public BigDecimal getValue() {
        return percentage;
    }
}
