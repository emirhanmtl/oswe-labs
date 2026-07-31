package com.campusnet.servlet;

import com.campusnet.dao.MessageDao;
import com.campusnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class MessageSendServlet extends HttpServlet {

    private final MessageDao messageDao = new MessageDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (SessionUtil.requireLogin(req, resp)) return;

        int recipientId;
        try {
            recipientId = Integer.parseInt(req.getParameter("recipient_id"));
        } catch (NumberFormatException e) {
            resp.setStatus(400);
            resp.getWriter().println("Invalid recipient.");
            return;
        }
        String subject = req.getParameter("subject");
        String body = req.getParameter("body");
        if (subject == null || subject.trim().isEmpty() || body == null || body.trim().isEmpty()) {
            resp.sendRedirect("/inbox");
            return;
        }

        try {
            messageDao.send(SessionUtil.currentUserId(req), recipientId, subject, body);
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().println("Failed to send message.");
            return;
        }
        resp.sendRedirect("/inbox");
    }
}
