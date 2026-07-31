package com.campusnet.dao;

import com.campusnet.db.Database;
import com.campusnet.model.Grade;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GradeDao {

    public List<Grade> findForStudent(int studentId) throws SQLException {
        String sql = "SELECT g.id, g.enrollment_id, g.grade, c.code AS course_code, c.title AS course_title " +
                     "FROM grades g " +
                     "JOIN enrollments e ON e.id = g.enrollment_id " +
                     "JOIN courses c ON c.id = e.course_id " +
                     "WHERE e.student_id = ? ORDER BY c.code";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Grade> out = new ArrayList<>();
                while (rs.next()) {
                    Grade g = new Grade();
                    g.setId(rs.getInt("id"));
                    g.setEnrollmentId(rs.getInt("enrollment_id"));
                    g.setGrade(rs.getString("grade"));
                    g.setCourseCode(rs.getString("course_code"));
                    g.setCourseTitle(rs.getString("course_title"));
                    out.add(g);
                }
                return out;
            }
        }
    }

    /** Roster view for a professor's own course: one row per enrolled student + their grade. */
    public List<Grade> findForCourse(int courseId) throws SQLException {
        String sql = "SELECT g.id, g.enrollment_id, g.grade, e.student_id, u.full_name AS student_name " +
                     "FROM enrollments e " +
                     "JOIN grades g ON g.enrollment_id = e.id " +
                     "JOIN users u ON u.id = e.student_id " +
                     "WHERE e.course_id = ? ORDER BY u.full_name";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Grade> out = new ArrayList<>();
                while (rs.next()) {
                    Grade g = new Grade();
                    g.setId(rs.getInt("id"));
                    g.setEnrollmentId(rs.getInt("enrollment_id"));
                    g.setGrade(rs.getString("grade"));
                    g.setStudentId(rs.getInt("student_id"));
                    g.setStudentName(rs.getString("student_name"));
                    out.add(g);
                }
                return out;
            }
        }
    }

    public void ensureGradeRow(int enrollmentId) throws SQLException {
        String sql = "SELECT id FROM grades WHERE enrollment_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return;
            }
        }
        String insert = "INSERT INTO grades (enrollment_id, grade) VALUES (?, NULL)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setInt(1, enrollmentId);
            ps.executeUpdate();
        }
    }

    /**
     * Set a grade for one enrollment. The servlet calling this is responsible for verifying
     * the enrollment belongs to a course this professor actually teaches - this DAO method
     * itself just runs a parameterized UPDATE, no SQL injection here.
     */
    public void setGrade(int enrollmentId, String grade) throws SQLException {
        String sql = "UPDATE grades SET grade = ?, graded_at = CURRENT_TIMESTAMP WHERE enrollment_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, grade);
            ps.setInt(2, enrollmentId);
            ps.executeUpdate();
        }
    }

    public Integer findCourseIdForEnrollment(int enrollmentId) throws SQLException {
        String sql = "SELECT course_id FROM enrollments WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return null;
    }
}
