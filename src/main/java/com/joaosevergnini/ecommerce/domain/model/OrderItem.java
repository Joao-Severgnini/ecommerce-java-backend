package com.joaosevergnini.ecommerce.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class OrderItem {
    private final Long id;
    private final Long productId;
    private final BigDecimal price;
    private int quantity;

    // Constructor without id for new items
    public OrderItem(Long productId, BigDecimal price, int quantity) {
        this.id = null;

        Objects.requireNonNull(productId, "productId must not be null");
        Objects.requireNonNull(price, "price must not be null");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");

        this.productId = productId;
        this.price = price.setScale(2, RoundingMode.HALF_UP);
        this.quantity = quantity;
    }

    // Constructor with id for existing items in DB
    public OrderItem(Long id, Long productId, BigDecimal price, int quantity) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(productId, "productId must not be null");
        Objects.requireNonNull(price, "price must not be null");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");

        this.id = id;
        this.productId = productId;
        this.price = price.setScale(2, RoundingMode.HALF_UP);
        this.quantity = quantity;
    }

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public BigDecimal getPrice() { return price; }
    public int getQuantity() { return quantity; }

    public void changeQuantity(int newQuantity){
        if (newQuantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        this.quantity = newQuantity;
    }

    // Retorna o preço total deste item (unitPrice * quantity)
    public BigDecimal getTotalPrice(){
        return price.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
    }
}
