package com.joaosevergnini.ecommerce.domain.discount;

import java.math.BigDecimal;

/**
 * Represents a discount that can be applied to a monetary value.
 * The apply method takes a BigDecimal value and returns the discounted value.
 * (2 decimal places, HALF_UP rounding is recommended when implementing this interface)
 **/
public interface Discount {
    BigDecimal apply(BigDecimal value);
}
