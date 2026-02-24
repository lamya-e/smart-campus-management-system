package com.smartcampus.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Supabase (PostgreSQL) connection singleton.
 * Reads database URL, username, and password from config.properties or .env
 * — never hardcoded. Schema is applied separately via schema.sql in Supabase SQL Editor.
 */
public final class DBConnection {

    private static Connection connection;

    private DBConnection() {
    }

    /**
     * Initializes the connection to Supabase/PostgreSQL using config from
     * config.properties or .env (database.url, database.username, database.password).
     */
    public static void initialize() {
        if (connection != null) {
            return;
        }
        try {
            Properties config = ConfigLoader.load();
            String url = config.getProperty(ConfigLoader.KEY_URL).trim();
            String username = config.getProperty(ConfigLoader.KEY_USERNAME, "").trim();
            String password = config.getProperty(ConfigLoader.KEY_PASSWORD, "").trim();
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database. Check URL, username, and password.", e);
        }
    }

    public static Connection getConnection() {
        if (connection == null) {
            initialize();
        }
        return connection;
    }

    public static void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // ignore on shutdown
            }
            connection = null;
        }
    }
}
