package com.joaosevergnini.ecommerce.application.service;

import com.joaosevergnini.ecommerce.domain.model.Order;
import com.joaosevergnini.ecommerce.domain.model.OrderItem;
import com.joaosevergnini.ecommerce.domain.model.Product;
import com.joaosevergnini.ecommerce.infrastructure.pesistence.connection.DatabaseConnection;
import com.joaosevergnini.ecommerce.infrastructure.pesistence.repository.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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

    public Order createOrder(Order order){
        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
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
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Rollback failed", ex);
                }
            }
            throw new RuntimeException("Error creating order", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException("Error closing connection", e);
                }
            }
        }
    }

    public Optional<Order> findOrderById(Long orderId) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            Optional<Order> orderOpt = orderRepository.finbyId(conn, orderId);
            if (orderOpt.isEmpty()) {
                return  Optional.empty();
            }

            Order order = orderOpt.get();

            orderItemRepository.findByOrderId(conn, order.getId())
                    .forEach(order::addItem);

            return Optional.of(order);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding order", e);
        }
    }

    public List<Order> findOrdersByCustomerId(Long customerId) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            List<Order> orders = orderRepository.findByCustomerId(conn, customerId);

            for (Order order : orders) {
                orderItemRepository.findByOrderId(conn, order.getId())
                        .forEach(order::addItem);
            }

            return orders;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding orders by customer ID", e);
        }
    }


}

