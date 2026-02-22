package com.smartcampus.service;

import com.smartcampus.model.Grade;
import com.smartcampus.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CRUD for grades. Can compute average per student or per course.
 */
public class GradeService {

    public static List<Grade> findAll() {
        List<Grade> list = new ArrayList<>();
        String sql = "SELECT id, student_id, course_id, value FROM grade ORDER BY student_id, course_id";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load grades", e);
        }
        return list;
    }

    public static List<Grade> findByStudent(int studentId) {
        List<Grade> list = new ArrayList<>();
        String sql = "SELECT id, student_id, course_id, value FROM grade WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Grade g = mapRow(rs);
                    g.setCourse(CourseService.findById(rs.getInt("course_id")));
                    list.add(g);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load grades for student", e);
        }
        return list;
    }

    public static List<Grade> findByCourse(int courseId) {
        List<Grade> list = new ArrayList<>();
        String sql = "SELECT id, student_id, course_id, value FROM grade WHERE course_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Grade g = mapRow(rs);
                    g.setStudent(StudentService.findById(rs.getInt("student_id")));
                    list.add(g);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load grades for course", e);
        }
        return list;
    }

    public static Grade findForStudentAndCourse(int studentId, int courseId) {
        String sql = "SELECT id, student_id, course_id, value FROM grade WHERE student_id = ? AND course_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load grade", e);
        }
    }

    public static Grade save(Grade g) {
        Grade existing = findForStudentAndCourse(g.getStudentId(), g.getCourseId());
        if (existing != null) {
            g.setId(existing.getId());
            update(g);
            return g;
        }
        return insert(g);
    }

    private static Grade insert(Grade g) {
        String sql = "INSERT INTO grade (student_id, course_id, value) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, g.getStudentId());
            ps.setInt(2, g.getCourseId());
            ps.setDouble(3, g.getValue());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    g.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert grade", e);
        }
        return g;
    }

    private static void update(Grade g) {
        String sql = "UPDATE grade SET value = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, g.getValue());
            ps.setInt(2, g.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update grade", e);
        }
    }

    /**
     * Returns average grade for a student across all courses, or 0 if none.
     */
    public static double averageForStudent(int studentId) {
        String sql = "SELECT AVG(value) FROM grade WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getObject(1) != null ? rs.getDouble(1) : 0.0;
            }
        } catch (SQLException e) {
            return 0.0;
        }
    }

    /**
     * Returns overall average grade across all grades in the system.
     */
    public static double overallAverage() {
        String sql = "SELECT AVG(value) FROM grade";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() && rs.getObject(1) != null ? rs.getDouble(1) : 0.0;
        } catch (SQLException e) {
            return 0.0;
        }
    }

    public static int count() {
        String sql = "SELECT COUNT(*) FROM grade";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    private static Grade mapRow(ResultSet rs) throws SQLException {
        Grade g = new Grade();
        g.setId(rs.getInt("id"));
        g.setStudentId(rs.getInt("student_id"));
        g.setCourseId(rs.getInt("course_id"));
        g.setValue(rs.getDouble("value"));
        return g;
    }
}
