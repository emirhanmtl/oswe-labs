package com.campusnet.dao;

import com.campusnet.db.Database;
import com.campusnet.model.Announcement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementDao {

    public List<Announcement> findForCourse(int courseId) throws SQLException {
        String sql = "SELECT a.id, a.course_id, a.author_id, u.full_name AS author_name, " +
                     "a.title, a.body, a.created_at " +
                     "FROM announcements a JOIN users u ON u.id = a.author_id " +
                     "WHERE a.course_id = ? ORDER BY a.created_at DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Announcement> out = new ArrayList<>();
                while (rs.next()) {
                    Announcement a = new Announcement();
                    a.setId(rs.getInt("id"));
                    a.setCourseId(rs.getInt("course_id"));
                    a.setAuthorId(rs.getInt("author_id"));
                    a.setAuthorName(rs.getString("author_name"));
                    a.setTitle(rs.getString("title"));
                    a.setBody(rs.getString("body"));
                    a.setCreatedAt(String.valueOf(rs.getTimestamp("created_at")));
                    out.add(a);
                }
                return out;
            }
        }
    }

    public void insert(int courseId, int authorId, String title, String body) throws SQLException {
        String sql = "INSERT INTO announcements (course_id, author_id, title, body) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.setInt(2, authorId);
            ps.setString(3, title);
            ps.setString(4, body);
            ps.executeUpdate();
        }
    }
}
