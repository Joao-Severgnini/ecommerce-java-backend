package com.joaosevergnini.ecommerce.application.service;

import com.joaosevergnini.ecommerce.domain.model.Order;
import com.joaosevergnini.ecommerce.domain.model.OrderItem;
import com.joaosevergnini.ecommerce.domain.model.Product;
import com.joaosevergnini.ecommerce.infrastructure.pesistence.connection.DatabaseConnection;
import com.joaosevergnini.ecommerce.infrastructure.pesistence.repository.*;

import java.sql.Connection;

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
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            customerRepository.findById(conn, order.getCustomerId())
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

            Order savedOrder = orderRepository.save(conn, order);

            for (OrderItem item : order.getItems()) {
                Product product = productRepository.findById(conn, item.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("Product not found: " + item.getProductId()));

                if(product.getStock() < item.getQuantity()) {
                    throw new IllegalStateException("Insufficient stock for product ID: " + item.getProductId());
                }

                product.decreaseStock(item.getQuantity());
                productRepository.update(conn, product);

                orderItemRepository.save(conn, savedOrder.getId(), item);
            }

            conn.commit();
            return savedOrder;

        }catch (Exception e){
            throw new RuntimeException("Error creating order", e);
        }
    }


}

