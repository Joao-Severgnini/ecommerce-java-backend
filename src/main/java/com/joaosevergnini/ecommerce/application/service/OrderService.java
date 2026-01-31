package com.joaosevergnini.ecommerce.application.service;

import com.joaosevergnini.ecommerce.domain.model.Order;
import com.joaosevergnini.ecommerce.domain.model.OrderItem;
import com.joaosevergnini.ecommerce.domain.model.OrderStatus;
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

            Optional<Order> orderOpt = orderRepository.finById(conn, orderId);
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

    public void cancelOrder(Long orderId) {
        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            Order order = orderRepository.finById(conn, orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));

            if (order.getStatus() != OrderStatus.CREATED) {
                throw new IllegalStateException("Only CREATED orders can be canceled.");
            }

            List <OrderItem> items = orderItemRepository.findByOrderId(conn, orderId);

            for (OrderItem item : items) {
                Product product = productRepository.findById(conn, item.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("Product not found: " + item.getProductId()));

                product.increaseStock(item.getQuantity());
                productRepository.update(conn, product);
        }

            order.setStatus(OrderStatus.CANCELED);
            orderRepository.updateStatus(conn, orderId, OrderStatus.CANCELED);

            conn.commit();
    } catch (Exception e){
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException("Rollback failed", ex);
            }
            throw new RuntimeException("Error canceling order", e);
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                throw new RuntimeException("Error closing connection", e);
            }
        }
    }
    public void payorder(Order order) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            Order existingOrder = orderRepository.finById(conn, order.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));

            if (existingOrder.getStatus() != OrderStatus.CREATED) {
                throw new IllegalStateException("Only CREATED orders can be paid.");
            }

            orderRepository.updateStatus(conn, order.getId(), OrderStatus.PAID);

        } catch (SQLException e) {
            throw new RuntimeException("Error paying order", e);
        }
    }

    public void shipOrder(Order order) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            Order existingOrder = orderRepository.finById(conn, order.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));

            if (existingOrder.getStatus() != OrderStatus.PAID) {
                throw new IllegalStateException("Only PAID orders can be shipped.");
            }

            orderRepository.updateStatus(conn, order.getId(), OrderStatus.SHIPPED);

        } catch (SQLException e) {
            throw new RuntimeException("Error shipping order", e);
        }
    }

    public void deliverOrder(Order order) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            Order existingOrder = orderRepository.finById(conn, order.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));

            if (existingOrder.getStatus() != OrderStatus.SHIPPED) {
                throw new IllegalStateException("Only SHIPPED orders can be delivered.");
            }

            orderRepository.updateStatus(conn, order.getId(), OrderStatus.DELIVERED);

        } catch (SQLException e) {
            throw new RuntimeException("Error delivering order", e);
        }
    }
}

