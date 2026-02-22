package com.smartcampus.service;

import com.smartcampus.model.Teacher;
import com.smartcampus.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CRUD and lookup operations for teachers. Used when assigning teachers to courses.
 */
public class TeacherService {

    public static List<Teacher> findAll() {
        List<Teacher> list = new ArrayList<>();
        String sql = "SELECT id, name, email, department FROM teacher ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load teachers", e);
        }
        return list;
    }

    public static Teacher findById(int id) {
        String sql = "SELECT id, name, email, department FROM teacher WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load teacher", e);
        }
    }

    public static Teacher save(Teacher t) {
        if (t.getId() <= 0) {
            return insert(t);
        }
        update(t);
        return t;
    }

    private static Teacher insert(Teacher t) {
        String sql = "INSERT INTO teacher (name, email, department) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getName());
            ps.setString(2, t.getEmail());
            ps.setString(3, t.getDepartment());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    t.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert teacher", e);
        }
        return t;
    }

    private static void update(Teacher t) {
        String sql = "UPDATE teacher SET name = ?, email = ?, department = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getName());
            ps.setString(2, t.getEmail());
            ps.setString(3, t.getDepartment());
            ps.setInt(4, t.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update teacher", e);
        }
    }

    public static void delete(int id) {
        String sql = "DELETE FROM teacher WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete teacher", e);
        }
    }

    private static Teacher mapRow(ResultSet rs) throws SQLException {
        Teacher t = new Teacher();
        t.setId(rs.getInt("id"));
        t.setName(rs.getString("name"));
        t.setEmail(rs.getString("email"));
        t.setDepartment(rs.getString("department"));
        return t;
    }
}
