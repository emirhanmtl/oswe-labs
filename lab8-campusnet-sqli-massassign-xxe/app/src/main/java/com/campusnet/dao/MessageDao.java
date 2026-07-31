package com.campusnet.dao;

import com.campusnet.db.Database;
import com.campusnet.model.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageDao {

    public List<Message> findForRecipient(int recipientId) throws SQLException {
        String sql = "SELECT m.id, m.sender_id, u.full_name AS sender_name, m.recipient_id, " +
                     "m.subject, m.body, m.created_at " +
                     "FROM messages m JOIN users u ON u.id = m.sender_id " +
                     "WHERE m.recipient_id = ? ORDER BY m.created_at DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, recipientId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Message> out = new ArrayList<>();
                while (rs.next()) {
                    Message m = new Message();
                    m.setId(rs.getInt("id"));
                    m.setSenderId(rs.getInt("sender_id"));
                    m.setSenderName(rs.getString("sender_name"));
                    m.setRecipientId(rs.getInt("recipient_id"));
                    m.setSubject(rs.getString("subject"));
                    m.setBody(rs.getString("body"));
                    m.setCreatedAt(String.valueOf(rs.getTimestamp("created_at")));
                    out.add(m);
                }
                return out;
            }
        }
    }

    public void send(int senderId, int recipientId, String subject, String body) throws SQLException {
        String sql = "INSERT INTO messages (sender_id, recipient_id, subject, body) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, senderId);
            ps.setInt(2, recipientId);
            ps.setString(3, subject);
            ps.setString(4, body);
            ps.executeUpdate();
        }
    }
}
