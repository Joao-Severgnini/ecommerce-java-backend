package com.joaosevergnini.ecommerce;

import com.joaosevergnini.ecommerce.infrastructure.pesistence.connection.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseTest {

    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = """
                CREATE TABLE IF NOT EXISTS products (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    price DECIMAL(10,2) NOT NULL,
                    stock INT NOT NULL)
                """;

            stmt.execute(sql);

            System.out.println("Tabela PRODUCTS criada com sucesso!");

            String insertSql = """
                INSERT INTO products (name, price, stock)
                VALUES ('Notebook Gamer', 2500.00, 10)
                """;

            stmt.executeUpdate(insertSql);
            System.out.println("Produto inserido!");


            ResultSet rs = stmt.executeQuery("SELECT * FROM products");

            while (rs.next()) {
                System.out.println(
                        rs.getLong("id") + " | " +
                                rs.getString("name") + " | " +
                                rs.getBigDecimal("price")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

