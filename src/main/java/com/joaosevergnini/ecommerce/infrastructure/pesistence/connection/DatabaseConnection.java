package com.joaosevergnini.ecommerce.infrastructure.pesistence.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:h2:mem:ecommerce-db";
    // Keep the in-memory database alive across multiple connections in the same JVM
    // so that the schema created by DatabaseInitializer remains accessible.
    // DB_CLOSE_DELAY=-1 prevents H2 from dropping the DB when the connection closes.
    // See: https://www.h2database.com/html/features.html#in_memory_databases
    private static final String URL_WITH_DELAY = "jdbc:h2:mem:ecommerce-db;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private DatabaseConnection(){
        // Private constructor to prevent instantiation
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL_WITH_DELAY, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database", e);
        }
    }
}




