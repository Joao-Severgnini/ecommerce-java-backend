package com.joaosevergnini.ecommerce.infrastructure.pesistence.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:h2:mem:ecommerce-db";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private DatabaseConnection(){
        // Private constructor to prevent instantiation
    }

    public static Connection getConnection() throws SQLException{
            return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
