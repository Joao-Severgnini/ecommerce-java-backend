package com.joaosevergnini.ecommerce.domain.model;

import com.joaosevergnini.ecommerce.domain.discount.Discount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Request {
    private final Long id;
    private final Long customerId;
    private final List<ItemOrdered> items = new ArrayList<>();
    private RequestStatus status;
    private Discount discount;

    // Constructor without id for new requests
    public Request(Long customerId) {
        Objects.requireNonNull(customerId, "customerId");
        this.id = null;
        this.customerId = customerId;
        this.status = RequestStatus.CREATED;
        this.discount = null;
    }

    // Constructor with id for existing requests in DB
    public Request(Long id, Long customerId, RequestStatus status, Discount discount) {
        Objects.requireNonNull(customerId, "customerId");
        Objects.requireNonNull(status, "status");
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.discount = discount;
    }

    // Business methods would go here

    private ItemOrdered findItemById(Long itemId){
        return items.stream()
                .filter(item -> Objects.equals(item.getId(), itemId))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Item with ID " + itemId + " not found in the request."
                             )
                        );
    }

    public void addItem(ItemOrdered item){
        Objects.requireNonNull(item, "item");
        if (status != RequestStatus.CREATED){
            throw new IllegalStateException("Cannot add items when request status is: " + status);
        }
        items.add(item);
    }

    public void applyDiscount(Discount discount){
        Objects.requireNonNull(discount, "discount");
        if (status != RequestStatus.CREATED){
            throw new IllegalStateException("Cannot apply discount when request status is: " + status);
        } else if (this.discount != null) {
            throw new IllegalStateException("A discount has already been applied to this request.");
        }
        this.discount = discount;
    }

    public void changeItemQuantity(Long itemId, int newQuantity){
        if (status != RequestStatus.CREATED){
            throw new IllegalStateException("Cannot change item quantity when request status is: " + status);
        }
        ItemOrdered item = findItemById(itemId);
        item.changeQuantity(newQuantity);
    }

    public BigDecimal calculateTotal(){
        BigDecimal total = items.stream()
                .map(ItemOrdered::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (discount != null){
            total = Objects.requireNonNull(discount.apply(total), "discount.apply must not return null");
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public void pay(){
        if (status != RequestStatus.CREATED) {
            throw new IllegalStateException(
                    "Request cannot be paid in status: " + status
            );
        }
        if (calculateTotal().compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalStateException(
                    "Total must be greater than zero"
            );
        }
        status = RequestStatus.PAID;
    }

    public void ship() {
        if (status != RequestStatus.PAID) {
            throw new IllegalStateException(
                    "Request cannot be shipped in status: " + status
            );
        }
        status = RequestStatus.SHIPPED;
    }

    public void deliver() {
        if (status != RequestStatus.SHIPPED) {
            throw new IllegalStateException(
                    "Request cannot be delivered in status: " + status
            );
        }
        status = RequestStatus.DELIVERED;
    }

    public void cancel() {
        if (status == RequestStatus.CREATED || status == RequestStatus.PAID) {
            status = RequestStatus.CANCELED;
        } else {
            throw new IllegalStateException(
                    "Request cannot be canceled in status: " + status
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

    public RequestStatus getStatus() {
        return status;
    }

    public List<ItemOrdered> getItems() {
        return List.copyOf(items);
    }
}
