package com.campusnet.dao;

import com.campusnet.db.Database;
import com.campusnet.model.Course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDao {

    public List<Course> findAll() throws SQLException {
        String sql = "SELECT c.id, c.code, c.title, c.description, c.department, c.credits, " +
                     "c.instructor_id, u.full_name AS instructor_name " +
                     "FROM courses c JOIN users u ON u.id = c.instructor_id ORDER BY c.code";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapAll(rs);
        }
    }

    /** Safe, parameterized department filter used by the public catalog browse page. */
    public List<Course> findByDepartment(String department) throws SQLException {
        String sql = "SELECT c.id, c.code, c.title, c.description, c.department, c.credits, " +
                     "c.instructor_id, u.full_name AS instructor_name " +
                     "FROM courses c JOIN users u ON u.id = c.instructor_id " +
                     "WHERE c.department = ? ORDER BY c.code";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, department);
            try (ResultSet rs = ps.executeQuery()) {
                return mapAll(rs);
            }
        }
    }

    public Course findById(int id) throws SQLException {
        String sql = "SELECT c.id, c.code, c.title, c.description, c.department, c.credits, " +
                     "c.instructor_id, u.full_name AS instructor_name " +
                     "FROM courses c JOIN users u ON u.id = c.instructor_id WHERE c.id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapOne(rs);
            }
        }
        return null;
    }

    public List<Course> findByInstructor(int instructorId) throws SQLException {
        String sql = "SELECT c.id, c.code, c.title, c.description, c.department, c.credits, " +
                     "c.instructor_id, u.full_name AS instructor_name " +
                     "FROM courses c JOIN users u ON u.id = c.instructor_id " +
                     "WHERE c.instructor_id = ? ORDER BY c.code";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            try (ResultSet rs = ps.executeQuery()) {
                return mapAll(rs);
            }
        }
    }

    public void insert(String code, String title, String description, String department, int credits, int instructorId) throws SQLException {
        String sql = "INSERT INTO courses (code, title, description, department, credits, instructor_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setString(4, department);
            ps.setInt(5, credits);
            ps.setInt(6, instructorId);
            ps.executeUpdate();
        }
    }

    public Integer findIdByCode(String code) throws SQLException {
        String sql = "SELECT id FROM courses WHERE code = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return null;
    }

    private List<Course> mapAll(ResultSet rs) throws SQLException {
        List<Course> out = new ArrayList<>();
        while (rs.next()) out.add(mapOne(rs));
        return out;
    }

    private Course mapOne(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setId(rs.getInt("id"));
        c.setCode(rs.getString("code"));
        c.setTitle(rs.getString("title"));
        c.setDescription(rs.getString("description"));
        c.setDepartment(rs.getString("department"));
        c.setCredits(rs.getInt("credits"));
        c.setInstructorId(rs.getInt("instructor_id"));
        c.setInstructorName(rs.getString("instructor_name"));
        return c;
    }
}
