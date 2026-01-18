package com.joaosevergnini.ecommerce.infrastructure.pesistence.repository;

import com.joaosevergnini.ecommerce.domain.discount.Discount;
import com.joaosevergnini.ecommerce.domain.discount.FixedDiscount;
import com.joaosevergnini.ecommerce.domain.discount.PercentageDiscount;
import com.joaosevergnini.ecommerce.domain.model.Order;
import com.joaosevergnini.ecommerce.domain.model.OrderStatus;
import com.joaosevergnini.ecommerce.infrastructure.pesistence.connection.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderRepository {

    public Order save(Connection conn, Order order){
        String sql = """
            INSERT INTO orders (customer_id, status, discount_type , discount_value)
            VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = conn.prepareStatement(
                     sql,
                        PreparedStatement.RETURN_GENERATED_KEYS
             )) {

            stmt.setLong(1, order.getCustomerId());
            stmt.setString(2, order.getStatus().name());

            if (order.getDiscount() == null){
                stmt.setNull(3, java.sql.Types.VARCHAR);
                stmt.setNull(4, java.sql.Types.DECIMAL);
            } else {
                stmt.setString(3, order.getDiscount().getType());
                stmt.setBigDecimal(4, order.getDiscount().getValue());
            }

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                long generatedId = rs.getLong(1);

                return new Order(
                        generatedId,
                        order.getCustomerId(),
                        order.getStatus(),
                        order.getDiscount()
                );
            }

            throw new RuntimeException("Failed to retrieve generated order ID");

        } catch (SQLException e){
            throw new RuntimeException("Error saving order", e);
        }
    }

    public Optional<Order> finbyId(Connection conn, Long id) {
        String sql = """
            SELECT id, customer_id, status, discount_type, discount_value
            FROM orders
            WHERE id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Long orderId = rs.getLong("id");
                Long customerId = rs.getLong("customer_id");
                OrderStatus status =  OrderStatus.valueOf(rs.getString("status"));

                Discount discount = null;
                String discountType = rs.getString("discount_type");
                BigDecimal discountValue = rs.getBigDecimal("discount_value");

                if (discountType != null && discountValue != null){
                    if (discountType.equals("FIXED")) {
                        discount = new FixedDiscount(discountValue);
                    } else if (discountType.equals("PERCENTAGE")) {
                        discount = new PercentageDiscount(discountValue);
                    }
                }

                Order order = new Order(
                        orderId,
                        customerId,
                        status,
                        discount
                );

                return Optional.of(order);
            }

            return Optional.empty();
        }catch (SQLException e){
            throw new RuntimeException("Error finding order by ID", e);
        }
    }

    public List<Order> findByCustomerId(Connection conn, Long customerId) {
        String sql = """
            SELECT id, customer_id, status, discount_type, discount_value
            FROM orders
            WHERE customer_id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, customerId);

            ResultSet rs = stmt.executeQuery();
            List<Order> orders = new ArrayList<>();

            while (rs.next()) {
                Long orderId = rs.getLong("id");
                OrderStatus status = OrderStatus.valueOf(rs.getString("status"));

                Discount discount = null;
                String discountType = rs.getString("discount_type");
                BigDecimal discountValue = rs.getBigDecimal("discount_value");

                if (discountType != null && discountValue != null){
                    if (discountType.equals("FIXED")) {
                        discount = new FixedDiscount(discountValue);
                    } else if (discountType.equals("PERCENTAGE")) {
                        discount = new PercentageDiscount(discountValue);
                    }
                }

                Order order = new Order(
                    orderId,
                    customerId,
                    status,
                    discount
                );

                orders.add(order);
            }

            return orders;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding orders by customer ID", e);
        }
    }

    public List<Order> findAll(Connection conn) {
        String sql = """
            SELECT id, customer_id, status, discount_type, discount_value
            FROM orders
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            List<Order> orders = new ArrayList<>();

            while (rs.next()) {
                Long orderId = rs.getLong("id");
                Long customerId = rs.getLong("customer_id");
                OrderStatus status = OrderStatus.valueOf(rs.getString("status"));

                Discount discount = null;
                String discountType = rs.getString("discount_type");
                BigDecimal discountValue = rs.getBigDecimal("discount_value");

                if (discountType != null && discountValue != null){
                    if (discountType.equals("FIXED")) {
                        discount = new FixedDiscount(discountValue);
                    } else if (discountType.equals("PERCENTAGE")) {
                        discount = new PercentageDiscount(discountValue);
                    }
                }

                Order order = new Order(
                    orderId,
                    customerId,
                    status,
                    discount
                );

                orders.add(order);
            }

            return orders;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all orders", e);
        }
    }
}
