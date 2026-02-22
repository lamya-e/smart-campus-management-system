package com.smartcampus.service;

import com.smartcampus.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Simple authentication service for admin login.
 * In a future Spring Boot backend, this would be replaced by Spring Security.
 */
public class AuthService {

    /**
     * Validates admin credentials. Default: admin / admin.
     */
    public static boolean login(String username, String password) {
        if (username == null || username.isBlank() || password == null) {
            return false;
        }
        String sql = "SELECT id FROM admin WHERE username = ? AND password_hash = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }
}
