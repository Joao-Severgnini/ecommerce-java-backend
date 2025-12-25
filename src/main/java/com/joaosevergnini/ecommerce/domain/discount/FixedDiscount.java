package com.joaosevergnini.ecommerce.domain.discount;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FixedDiscount implements Discount{
    private final BigDecimal value;

    public FixedDiscount(BigDecimal value) { this.value = value; }

    @Override
    public BigDecimal apply(BigDecimal originalValue) {
        return originalValue.subtract(value).setScale(2, RoundingMode.HALF_UP);
    }
}
