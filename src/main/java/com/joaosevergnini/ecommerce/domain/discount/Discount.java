package com.joaosevergnini.ecommerce.domain.discount;

import java.math.BigDecimal;

public interface Discount {
    BigDecimal apply(BigDecimal value);
}
