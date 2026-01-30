package com.joaosevergnini.ecommerce.domain.model;

import com.joaosevergnini.ecommerce.domain.discount.Discount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Order {
    private final Long id;
    private final Long customerId;
    private final List<OrderItem> items = new ArrayList<>();
    private OrderStatus status;
    private Discount discount;

    // Constructor without id for new requests
    public Order(Long customerId) {
        Objects.requireNonNull(customerId, "customerId");
        this.id = null;
        this.customerId = customerId;
        this.status = OrderStatus.CREATED;
        this.discount = null;
    }

    // Constructor with id for existing requests in DB
    public Order(Long id, Long customerId, OrderStatus status, Discount discount) {
        Objects.requireNonNull(customerId, "customerId");
        Objects.requireNonNull(status, "status");
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.discount = discount;
    }

    // Business methods would go here

    private OrderItem findItemById(Long itemId){
        return items.stream()
                .filter(item -> Objects.equals(item.getId(), itemId))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Item with ID " + itemId + " not found in the request."
                             )
                        );
    }

    public void addItem(OrderItem item){
        Objects.requireNonNull(item, "item");
        if (status != OrderStatus.CREATED){
            throw new IllegalStateException("Cannot add items when request status is: " + status);
        }
        items.add(item);
    }

    public void setStatus(OrderStatus status){
        Objects.requireNonNull(status, "status");
        this.status = status;
    }

    public void applyDiscount(Discount discount){
        Objects.requireNonNull(discount, "discount");
        if (status != OrderStatus.CREATED){
            throw new IllegalStateException("Cannot apply discount when request status is: " + status);
        } else if (this.discount != null) {
            throw new IllegalStateException("A discount has already been applied to this request.");
        }
        this.discount = discount;
    }

    public void changeItemQuantity(Long itemId, int newQuantity){
        if (status != OrderStatus.CREATED){
            throw new IllegalStateException("Cannot change item quantity when request status is: " + status);
        }
        OrderItem item = findItemById(itemId);
        item.changeQuantity(newQuantity);
    }

    public BigDecimal calculateTotal(){
        BigDecimal total = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (discount != null){
            total = Objects.requireNonNull(discount.apply(total), "discount.apply must not return null");
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public void pay(){
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException(
                    "Order cannot be paid in status: " + status
            );
        }
        if (calculateTotal().compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalStateException(
                    "Total must be greater than zero"
            );
        }
        status = OrderStatus.PAID;
    }

    public void ship() {
        if (status != OrderStatus.PAID) {
            throw new IllegalStateException(
                    "Order cannot be shipped in status: " + status
            );
        }
        status = OrderStatus.SHIPPED;
    }

    public void deliver() {
        if (status != OrderStatus.SHIPPED) {
            throw new IllegalStateException(
                    "Order cannot be delivered in status: " + status
            );
        }
        status = OrderStatus.DELIVERED;
    }

    public void cancel() {
        if (status == OrderStatus.CREATED || status == OrderStatus.PAID) {
            status = OrderStatus.CANCELED;
        } else {
            throw new IllegalStateException(
                    "Order cannot be canceled in status: " + status
            );
        }
    }

    // Getters

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return List.copyOf(items);
    }

    public Discount getDiscount() {
        return discount;
    }
}
