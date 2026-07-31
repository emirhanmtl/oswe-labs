package com.campusnet.dao;

import com.campusnet.db.Database;
import com.campusnet.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password_hash, role, full_name, email, department, bio " +
                     "FROM users WHERE username = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public User findById(int id) throws SQLException {
        String sql = "SELECT id, username, password_hash, role, full_name, email, department, bio " +
                     "FROM users WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public List<User> findByRoleIn(String... roles) throws SQLException {
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < roles.length; i++) {
            if (i > 0) placeholders.append(",");
            placeholders.append("?");
        }
        String sql = "SELECT id, username, password_hash, role, full_name, email, department, bio " +
                     "FROM users WHERE role IN (" + placeholders + ") ORDER BY id";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < roles.length; i++) {
                ps.setString(i + 1, roles[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<User> out = new ArrayList<>();
                while (rs.next()) out.add(map(rs));
                return out;
            }
        }
    }

    public List<User> findAll() throws SQLException {
        String sql = "SELECT id, username, password_hash, role, full_name, email, department, bio FROM users ORDER BY id";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<User> out = new ArrayList<>();
            while (rs.next()) out.add(map(rs));
            return out;
        }
    }

    public void insert(String username, String passwordHash, String role, String fullName, String email) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, role, full_name, email, department, bio) " +
                     "VALUES (?, ?, ?, ?, ?, '', '')";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, role);
            ps.setString(4, fullName);
            ps.setString(5, email);
            ps.executeUpdate();
        }
    }

    /**
     * Persists every field on the bean, including role. Parameterized (no SQL injection here) -
     * the risk with this method isn't in the SQL, it's in *who is allowed to set which field
     * on the bean before it ever reaches this method*.
     */
    public void update(User user) throws SQLException {
        String sql = "UPDATE users SET full_name = ?, email = ?, department = ?, bio = ?, role = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getDepartment());
            ps.setString(4, user.getBio());
            ps.setString(5, user.getRole());
            ps.setInt(6, user.getId());
            ps.executeUpdate();
        }
    }

    public void setRole(int userId, String role) throws SQLException {
        String sql = "UPDATE users SET role = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRole(rs.getString("role"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setDepartment(rs.getString("department"));
        u.setBio(rs.getString("bio"));
        return u;
    }
}
