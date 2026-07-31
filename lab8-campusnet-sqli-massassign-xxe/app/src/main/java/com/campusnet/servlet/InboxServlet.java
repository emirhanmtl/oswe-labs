package com.campusnet.servlet;

import com.campusnet.dao.MessageDao;
import com.campusnet.dao.UserDao;
import com.campusnet.model.Message;
import com.campusnet.model.User;
import com.campusnet.util.HtmlUtil;
import com.campusnet.util.Page;
import com.campusnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Lightweight inbox so students and professors can message each other about a
 * course. Nothing sensitive here beyond normal message content, and messages are
 * only ever looked up by the recipient's own session id.
 */
public class InboxServlet extends HttpServlet {

    private final MessageDao messageDao = new MessageDao();
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (SessionUtil.requireLogin(req, resp)) return;

        List<Message> messages;
        List<User> people;
        try {
            messages = messageDao.findForRecipient(SessionUtil.currentUserId(req));
            people = userDao.findAll();
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().println("Error loading inbox.");
            return;
        }

        resp.setContentType("text/html");
        StringBuilder sb = new StringBuilder();
        sb.append(Page.header("Inbox", req));

        sb.append("<form method=\"post\" action=\"/messages/send\">")
          .append("<label>To: <select name=\"recipient_id\">");
        for (User u : people) {
            if (u.getId() == SessionUtil.currentUserId(req)) continue;
            sb.append("<option value=\"").append(u.getId()).append("\">")
              .append(HtmlUtil.escape(u.getFullName())).append(" (").append(HtmlUtil.escape(u.getRole())).append(")")
              .append("</option>");
        }
        sb.append("</select></label><br>")
          .append("<label>Subject: <input type=\"text\" name=\"subject\"></label><br>")
          .append("<label>Message:<br><textarea name=\"body\" rows=\"3\" cols=\"50\"></textarea></label><br>")
          .append("<button type=\"submit\">Send</button></form>");

        sb.append("<h2>Received</h2>");
        for (Message m : messages) {
            sb.append("<div class=\"card\"><strong>").append(HtmlUtil.escape(m.getSubject())).append("</strong>")
              .append(" <em>from ").append(HtmlUtil.escape(m.getSenderName())).append("</em>")
              .append("<p>").append(HtmlUtil.escape(m.getBody())).append("</p></div>");
        }

        sb.append(Page.footer());
        resp.getWriter().println(sb);
    }
}
