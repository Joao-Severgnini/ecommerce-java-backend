package com.joaosevergnini.ecommerce.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Product {
    private final Long id;
    private final String name;
    private BigDecimal price;
    int stock;

    // Constructor without id for new products
    public Product(String name, BigDecimal price, int stock){
        Objects.requireNonNull(name, "name");
        if (name.isBlank()) throw new IllegalArgumentException("name must not be blank");
        Objects.requireNonNull(price, "price");
        if (price.signum() < 0) throw new IllegalArgumentException("price must be non-negative");
        if (stock < 0) throw new IllegalArgumentException("stock must be non-negative");

        this.id = null;
        this.name = name;
        this.price = price.setScale(2, RoundingMode.HALF_UP);
        this.stock = stock;
    }

    // Constructor with id for existing products of DB
    public Product(long id, String name, BigDecimal price, int stock) {
        Objects.requireNonNull(name, "name");
        if (name.isBlank()) throw new IllegalArgumentException("name must not be blank");
        Objects.requireNonNull(price, "price");
        if (price.signum() < 0) throw new IllegalArgumentException("price must be non-negative");
        if (stock < 0) throw new IllegalArgumentException("stock must be non-negative");

        this.id = id;
        this.name = name;
        this.price = price.setScale(2, RoundingMode.HALF_UP);
        this.stock = stock;
    }

    public BigDecimal getPrice(){ return price; }
    public void setPrice(BigDecimal price){
        Objects.requireNonNull(price, "price");
        if (price.signum() < 0) throw new IllegalArgumentException("price must be non-negative");
        this.price = price.setScale(2, RoundingMode.HALF_UP);
    }
    public Long getId(){ return id; }
    public String getName() { return name; }
    public int getStock() { return stock; }
    public void decreaseStock(int quantity){
        if (quantity <= 0){
            throw new IllegalArgumentException("Quantity to decrease must be positive");
        }
        if (this.stock < quantity){
            throw new IllegalStateException("Insufficient stock to decrease by " + quantity);
        }
        this.stock -= quantity;
    }
    public void increaseStock(int quantity){
        if (quantity <= 0){
            throw new IllegalArgumentException("Quantity to increase must be positive");
        }
        this.stock += quantity;
    }
}
