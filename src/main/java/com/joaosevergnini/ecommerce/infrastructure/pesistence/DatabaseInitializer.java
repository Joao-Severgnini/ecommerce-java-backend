package com.joaosevergnini.ecommerce.infrastructure.pesistence;

import com.joaosevergnini.ecommerce.infrastructure.pesistence.connection.DatabaseConnection;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void init() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             InputStream is = DatabaseInitializer.class
                     .getClassLoader()
                     .getResourceAsStream("db/schema.sql")) {

            if (is == null) {
                throw new RuntimeException("schema.sql not found in classpath");
            }

            String sql = new String(is.readAllBytes());
            stmt.execute(sql);

        } catch (Exception e) {
            throw new RuntimeException("Error initializing database", e);
        }
    }
}
