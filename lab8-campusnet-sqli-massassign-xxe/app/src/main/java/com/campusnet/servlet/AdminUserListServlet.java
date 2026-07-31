package com.campusnet.servlet;

import com.campusnet.dao.UserDao;
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

public class AdminUserListServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (SessionUtil.requireRole(req, resp, "admin")) return;

        List<User> users;
        try {
            users = userDao.findAll();
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().println("Error loading users.");
            return;
        }

        resp.setContentType("text/html");
        StringBuilder sb = new StringBuilder();
        sb.append(Page.header("All Users", req));
        sb.append("<table border=\"1\" cellpadding=\"6\"><tr><th>ID</th><th>Username</th><th>Role</th><th>Full name</th><th>Email</th><th>Department</th></tr>");
        for (User u : users) {
            sb.append("<tr>")
              .append("<td>").append(u.getId()).append("</td>")
              .append("<td>").append(HtmlUtil.escape(u.getUsername())).append("</td>")
              .append("<td>").append(HtmlUtil.escape(u.getRole())).append("</td>")
              .append("<td>").append(HtmlUtil.escape(u.getFullName())).append("</td>")
              .append("<td>").append(HtmlUtil.escape(u.getEmail())).append("</td>")
              .append("<td>").append(HtmlUtil.escape(u.getDepartment())).append("</td>")
              .append("</tr>");
        }
        sb.append("</table>");
        sb.append(Page.footer());
        resp.getWriter().println(sb);
    }
}
