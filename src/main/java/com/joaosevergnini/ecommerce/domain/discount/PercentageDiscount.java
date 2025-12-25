package com.joaosevergnini.ecommerce.domain.discount;

import java.math.BigDecimal;

public class PercentageDiscount implements Discount{
    private final BigDecimal percentage;

    public PercentageDiscount(BigDecimal percentage) { this.percentage = percentage; }

    @Override
    public BigDecimal apply(BigDecimal originalValue){
        return originalValue.subtract(originalValue.multiply(percentage));
    }
}
