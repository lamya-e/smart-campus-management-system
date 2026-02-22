package com.smartcampus.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.nio.file.Paths;

/**
 * SQLite database connection singleton. Creates schema on first use.
 * Database file is stored in user directory for portability.
 */
public final class DBConnection {

    private static final String DB_NAME = "smart_campus.db";
    private static Connection connection;

    private DBConnection() {
    }

    /**
     * Initializes the database: loads driver, creates file if needed, runs schema.
     */
    public static void initialize() {
        try {
            String path = Paths.get(System.getProperty("user.dir"), DB_NAME).toString();
            String url = "jdbc:sqlite:" + path;
            connection = DriverManager.getConnection(url);
            createSchema();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    private static void createSchema() throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute(
                "CREATE TABLE IF NOT EXISTS admin (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  username TEXT UNIQUE NOT NULL," +
                "  password_hash TEXT NOT NULL" +
                ")"
            );
            st.execute(
                "CREATE TABLE IF NOT EXISTS student (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  name TEXT NOT NULL," +
                "  email TEXT UNIQUE NOT NULL," +
                "  major TEXT" +
                ")"
            );
            st.execute(
                "CREATE TABLE IF NOT EXISTS teacher (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  name TEXT NOT NULL," +
                "  email TEXT UNIQUE NOT NULL," +
                "  department TEXT" +
                ")"
            );
            st.execute(
                "CREATE TABLE IF NOT EXISTS course (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  title TEXT NOT NULL," +
                "  teacher_id INTEGER REFERENCES teacher(id)" +
                ")"
            );
            st.execute(
                "CREATE TABLE IF NOT EXISTS enrollment (" +
                "  student_id INTEGER NOT NULL," +
                "  course_id INTEGER NOT NULL," +
                "  PRIMARY KEY (student_id, course_id)," +
                "  FOREIGN KEY (student_id) REFERENCES student(id)," +
                "  FOREIGN KEY (course_id) REFERENCES course(id)" +
                ")"
            );
            st.execute(
                "CREATE TABLE IF NOT EXISTS grade (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  student_id INTEGER NOT NULL," +
                "  course_id INTEGER NOT NULL," +
                "  value REAL NOT NULL," +
                "  UNIQUE(student_id, course_id)," +
                "  FOREIGN KEY (student_id) REFERENCES student(id)," +
                "  FOREIGN KEY (course_id) REFERENCES course(id)" +
                ")"
            );
            // Default admin if none exists (password: admin)
            st.execute(
                "INSERT OR IGNORE INTO admin (username, password_hash) VALUES ('admin', 'admin')"
            );
            // Seed one teacher for demo
            st.execute(
                "INSERT OR IGNORE INTO teacher (id, name, email, department) VALUES (1, 'Dr. Jane Smith', 'jane.smith@campus.edu', 'Computer Science')"
            );
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
