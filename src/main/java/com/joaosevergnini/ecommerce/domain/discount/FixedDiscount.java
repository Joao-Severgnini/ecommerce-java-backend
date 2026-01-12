package com.joaosevergnini.ecommerce.domain.discount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class FixedDiscount implements Discount{
    private final BigDecimal value;

    public FixedDiscount(BigDecimal value) {
        Objects.requireNonNull(value, "value");
        if (value.signum() < 0) throw new IllegalArgumentException("value must be non-negative");
        this.value = value.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal apply(BigDecimal originalValue) {
        Objects.requireNonNull(originalValue, "originalValue");
        return originalValue.subtract(value).setScale(2, RoundingMode.HALF_UP);
    }
    
    public String getType() {
        return "FIXED";
    }

    public BigDecimal getValue() {
        return value;
    }
}
