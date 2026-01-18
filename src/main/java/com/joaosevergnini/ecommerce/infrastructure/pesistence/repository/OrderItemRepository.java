package com.joaosevergnini.ecommerce.infrastructure.pesistence.repository;

import com.joaosevergnini.ecommerce.domain.model.OrderItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class OrderItemRepository {

    public OrderItem save(Connection conn, Long orderId, OrderItem item){
        String sql = """
            INSERT INTO order_items (order_id, product_id, price, quantity)
            VALUES (?, ?, ?, ?)
        """;

       try (PreparedStatement stmt = conn.prepareStatement(
                    sql,
                    PreparedStatement.RETURN_GENERATED_KEYS
            )) {

           stmt.setLong(1, orderId);
           stmt.setLong(2, item.getProductId());
           stmt.setBigDecimal(3, item.getPrice());
           stmt.setInt(4, item.getQuantity());

           stmt.executeUpdate();

           ResultSet rs = stmt.getGeneratedKeys();

              if (rs.next()) {
                long generatedId = rs.getLong(1);

                return new OrderItem(
                          generatedId,
                          item.getProductId(),
                          item.getPrice(),
                          item.getQuantity()
                );
              }
           throw new RuntimeException("Failed to retrieve generated order item ID");
       } catch (SQLException e){
           throw new RuntimeException("Error saving order item", e);
       }
    }

    public Optional<OrderItem> findById(Connection conn, Long id){
        String sql = """
            SELECT id, product_id, price, quantity
            FROM order_items
            WHERE id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                OrderItem item = new OrderItem(
                    rs.getLong("id"),
                    rs.getLong("product_id"),
                    rs.getBigDecimal("price"),
                    rs.getInt("quantity")
                );
                return Optional.of(item);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding order item", e);
        }
    }

    public List<OrderItem> findByOrderId(Connection conn, Long orderId){
        String sql = """
            SELECT id, product_id, price, quantity
            FROM order_items
            WHERE order_id = ?
        """;

        try(PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setLong(1, orderId);
            ResultSet rs = stmt.executeQuery();
            List<OrderItem> items = new java.util.ArrayList<>();

            while (rs.next()){
                OrderItem item = new OrderItem(
                    rs.getLong("id"),
                    rs.getLong("product_id"),
                    rs.getBigDecimal("price"),
                    rs.getInt("quantity")
                );
                items.add(item);
            }
            return items;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding order items by order ID", e);
        }
    }
}
