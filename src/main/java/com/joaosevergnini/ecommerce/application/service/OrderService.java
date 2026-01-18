package com.joaosevergnini.ecommerce.application.service;

import com.joaosevergnini.ecommerce.domain.model.Order;
import com.joaosevergnini.ecommerce.domain.model.OrderItem;
import com.joaosevergnini.ecommerce.domain.model.Product;
import com.joaosevergnini.ecommerce.infrastructure.pesistence.repository.*;

public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository,
            CustomerRepository customerRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    public Order CreateOrder(Order order){

            customerRepository.findById(conn, order.getCustomerId())
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        Order savedOrder = orderRepository.save(order);

        for (OrderItem item : order.getItems()) {

            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Product not found: " + item.getProductId())
                    );

            product.decreaseStock(item.getQuantity());
            productRepository.update(product);

            orderItemRepository.save(savedOrder.getId(), item);
        }

        return savedOrder;
    }
}
