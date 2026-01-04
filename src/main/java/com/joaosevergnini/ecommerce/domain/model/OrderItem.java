package com.joaosevergnini.ecommerce.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class OrderItem {
    private final Long id;
    private final Product product;
    private int quantity;

    // Constructor without id for new items
    public OrderItem(Product product, int quantity) {
        this.id = null;
        this.product = Objects.requireNonNull(product, "product");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        this.quantity = quantity;
    }

    // Constructor with id for existing items in DB
    public OrderItem(Long id, Product product, int quantity) {
        this.id = id;
        this.product = Objects.requireNonNull(product, "product");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        this.quantity = quantity;
    }

    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }

    public void changeQuantity(int newQuantity){
        if (newQuantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        this.quantity = newQuantity;
    }

    // Retorna o preço total deste item (unitPrice * quantity)
    public BigDecimal getTotalPrice(){
        BigDecimal unitPrice = product.getPrice();
        return unitPrice.multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
