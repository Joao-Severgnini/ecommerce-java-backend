package com.joaosevergnini.ecommerce.infrastructure.pesistence.repository;

import com.joaosevergnini.ecommerce.domain.model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CustomerRepository {

    public Customer save(Connection conn, Customer customer){
        String sql = """
            INSERT INTO customers (name, email)
            VALUES (?, ?)
        """;

        try (PreparedStatement stmt = conn.prepareStatement(
                     sql,
                     PreparedStatement.RETURN_GENERATED_KEYS
             )) {

            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getEmail());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()){
                long generatedId = rs.getLong(1);

                return new Customer(
                        generatedId,
                        customer.getName(),
                        customer.getEmail()
                );
            }
            throw new RuntimeException("Failed to retrieve generated customer ID");
        } catch (SQLException e){
            throw new RuntimeException("Error saving customer", e);
        }
    }

    public Customer update(Connection conn, Customer customer){
        String sql = """
            UPDATE customers
            SET name = ?, email = ?
            WHERE id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getEmail());
            stmt.setLong(3, customer.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No customer found with ID: " + customer.getId());
            }
            return customer;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating customer", e);
        }
    }

    public Optional<Customer> findById(Connection conn, Long id){
        String sql = """
            SELECT id, name, email
            FROM customers
            WHERE id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()){
                Customer customer = new Customer(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email")
                );
                return Optional.of(customer);
            }
            return Optional.empty();
        } catch (SQLException e){
            throw new RuntimeException("Error finding customer by ID", e);
        }
    }

    public Optional<Customer> findByEmail(Connection conn, String email){
        String sql = """
            SELECT id, name, email
            FROM customers
            WHERE email = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()){
                Customer customer = new Customer(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email")
                );
                return Optional.of(customer);
            }
            return Optional.empty();
        } catch (SQLException e){
            throw new RuntimeException("Error finding customer by email", e);
        }
    }

    public List<Customer> findAll(Connection conn){
        String sql = """
            SELECT id, name, email
            FROM customers
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            List<Customer> customers = new java.util.ArrayList<>();

            while (rs.next()){
                customers.add(new Customer(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("email")
                ));
            }
            return customers;
        } catch (SQLException e){
            throw new RuntimeException("Error finding all customers", e);
        }
    }

    public boolean deleteById(Connection conn, Long id){
        String sql = """
            DELETE FROM customers
            WHERE id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e){
            throw new RuntimeException("Error deleting customer by ID", e);
        }
    }
}
