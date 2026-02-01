package com.joaosevergnini.ecommerce.application.service;

import com.joaosevergnini.ecommerce.domain.model.Product;
import com.joaosevergnini.ecommerce.infrastructure.pesistence.connection.DatabaseConnection;
import com.joaosevergnini.ecommerce.infrastructure.pesistence.repository.ProductRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(Product product) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return productRepository.save(conn, product);
        }catch (Exception e){
            throw new RuntimeException("Error creating product", e);
        }
    }

    public Product findbyId(Long id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return productRepository.findById(conn, id)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        } catch (Exception e) {
            throw new RuntimeException("Error finding product", e);
        }
    }

    public List<Product> findAll() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return productRepository.findAll(conn);
        } catch (Exception e) {
            throw new RuntimeException("Error finding products", e);
        }
    }

    public void increaseStock(Long productId, int amount) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            Product product = productRepository.findById(conn, productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            product.increaseStock(amount);
            productRepository.update(conn, product);

        } catch (Exception e) {
            throw new RuntimeException("Error increasing product stock", e);
        }
    }

    public void decreaseStock(Long productId, int amount) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            Product product = productRepository.findById(conn, productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            product.decreaseStock(amount);
            productRepository.update(conn, product);

        } catch (Exception e) {
            throw new RuntimeException("Error decreasing product stock", e);
        }
    }

    public void updatePrice(Long productId, BigDecimal newPrice) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            Product product = productRepository.findById(conn, productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            product.setPrice(newPrice);
            productRepository.update(conn, product);

        }catch (Exception e) {
            throw new RuntimeException("Error updating product price", e);
        }
    }
}
