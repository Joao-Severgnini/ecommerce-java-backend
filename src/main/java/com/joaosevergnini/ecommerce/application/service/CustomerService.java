package com.joaosevergnini.ecommerce.application.service;

import com.joaosevergnini.ecommerce.domain.model.Customer;
import com.joaosevergnini.ecommerce.infrastructure.pesistence.connection.DatabaseConnection;
import com.joaosevergnini.ecommerce.infrastructure.pesistence.repository.CustomerRepository;

import java.sql.Connection;
import java.util.List;

public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer createCustomer(Customer customer) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return customerRepository.save(conn, customer);
        } catch (Exception e) {
            throw new RuntimeException("Error creating customer", e);
        }
    }

    public Customer findById(Long id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return customerRepository.findById(conn, id)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        } catch (Exception e) {
            throw new RuntimeException("Error finding customer", e);
        }
    }

    public Customer findByEmail(String email) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return customerRepository.findByEmail(conn, email)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        } catch (Exception e) {
            throw new RuntimeException("Error finding customer by email", e);
        }
    }

    public Customer updateCustomer(Customer customer) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return customerRepository.update(conn, customer);
        } catch (Exception e) {
            throw new RuntimeException("Error updating customer", e);
        }
    }

    public List<Customer> findAllCustomers() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return customerRepository.findAll(conn);
        } catch (Exception e) {
            throw new RuntimeException("Error finding customers", e);
        }
    }

    public void deleteCustomer(Long id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            customerRepository.deleteById(conn, id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting customer", e);
        }
    }


}
