package com.joaosevergnini.ecommerce.infrastructure.pesistence.repository;

import com.joaosevergnini.ecommerce.domain.model.Product;
import com.joaosevergnini.ecommerce.infrastructure.pesistence.connection.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductRepository {

    public Product save(Connection conn, Product product) {
        String sql = """
            INSERT INTO products (name, price, stock)
            VALUES (?, ?, ?)
        """;

        try (PreparedStatement stmt = conn.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS
        )) {
            stmt.setString(1, product.getName());
            stmt.setBigDecimal(2, product.getPrice());
            stmt.setInt(3, product.getStock());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                long generatedId = rs.getLong(1);

                return new Product(
                        generatedId,
                        product.getName(),
                        product.getPrice(),
                        product.getStock()
                );
            }

            throw new RuntimeException("Failed to retrieve generated product ID");

        }catch (SQLException e){
            throw new RuntimeException("Error saving product", e);
        }

    }

    public void update(Connection conn, Product product) {
        String sql = """
            UPDATE products
            SET name = ?, price = ?, stock = ?
            WHERE id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getName());
            stmt.setBigDecimal(2, product.getPrice());
            stmt.setInt(3, product.getStock());
            stmt.setLong(4, product.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No product found with ID: " + product.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating product", e);
        }
    }

    public Optional<Product> findById(Connection conn, Long id){
        String sql = """
            SELECT id, name, price, stock
            FROM products
            WHERE id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Product product = new Product(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getBigDecimal("price"),
                        rs.getInt("stock")
                );
                return Optional.of(product);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding product by ID", e);
        }
    }

    public List<Product> findAll(Connection conn){
        String sql = """
            SELECT id, name, price, stock
            FROM products
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            List<Product> products = new ArrayList<>();

            while (rs.next()){
                products.add(new Product(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getBigDecimal("price"),
                        rs.getInt("stock")
                ));
            }
            return products;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all products", e);
        }
    }

    public boolean deleteById(Connection conn, Long id){
        String sql = """
            DELETE FROM products
            WHERE id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting product by ID", e);
        }
    }
}
