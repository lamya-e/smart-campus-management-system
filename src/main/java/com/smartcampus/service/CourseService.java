package com.smartcampus.service;

import com.smartcampus.model.Course;
import com.smartcampus.model.Student;
import com.smartcampus.model.Teacher;
import com.smartcampus.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CRUD for courses, teacher assignment, and student enrollment.
 */
public class CourseService {

    public static List<Course> findAll() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT id, title, teacher_id FROM course ORDER BY title";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Course c = mapRow(rs);
                Integer tid = (Integer) rs.getObject("teacher_id");
                if (tid != null) {
                    c.setAssignedTeacher(TeacherService.findById(tid));
                }
                list.add(c);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load courses", e);
        }
        return list;
    }

    public static Course findById(int id) {
        String sql = "SELECT id, title, teacher_id FROM course WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Course c = mapRow(rs);
                Integer tid = (Integer) rs.getObject("teacher_id");
                if (tid != null) {
                    c.setAssignedTeacher(TeacherService.findById(tid));
                }
                c.setStudents(getEnrolledStudents(id));
                return c;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load course", e);
        }
    }

    public static Course save(Course c) {
        if (c.getId() <= 0) {
            return insert(c);
        }
        update(c);
        return c;
    }

    private static Course insert(Course c) {
        String sql = "INSERT INTO course (title, teacher_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getTitle());
            ps.setObject(2, c.getTeacherId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    c.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert course", e);
        }
        return c;
    }

    private static void update(Course c) {
        String sql = "UPDATE course SET title = ?, teacher_id = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getTitle());
            ps.setObject(2, c.getTeacherId());
            ps.setInt(3, c.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update course", e);
        }
    }

    public static void delete(int id) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement("DELETE FROM enrollment WHERE course_id = ?");
                 PreparedStatement ps2 = conn.prepareStatement("DELETE FROM grade WHERE course_id = ?");
                 PreparedStatement ps3 = conn.prepareStatement("DELETE FROM course WHERE id = ?")) {
                ps1.setInt(1, id);
                ps1.executeUpdate();
                ps2.setInt(1, id);
                ps2.executeUpdate();
                ps3.setInt(1, id);
                ps3.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete course", e);
        }
    }

    public static void enrollStudent(int courseId, int studentId) {
        String sql = "INSERT OR IGNORE INTO enrollment (student_id, course_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to enroll student", e);
        }
    }

    public static void unenrollStudent(int courseId, int studentId) {
        String sql = "DELETE FROM enrollment WHERE student_id = ? AND course_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to unenroll student", e);
        }
    }

    public static List<Student> getEnrolledStudents(int courseId) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.id, s.name, s.email, s.major FROM student s " +
                     "INNER JOIN enrollment e ON s.id = e.student_id WHERE e.course_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Student s = new Student();
                    s.setId(rs.getInt("id"));
                    s.setName(rs.getString("name"));
                    s.setEmail(rs.getString("email"));
                    s.setMajor(rs.getString("major"));
                    list.add(s);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load enrolled students", e);
        }
        return list;
    }

    public static int count() {
        String sql = "SELECT COUNT(*) FROM course";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    private static Course mapRow(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setId(rs.getInt("id"));
        c.setTitle(rs.getString("title"));
        c.setTeacherId((Integer) rs.getObject("teacher_id"));
        return c;
    }
}
