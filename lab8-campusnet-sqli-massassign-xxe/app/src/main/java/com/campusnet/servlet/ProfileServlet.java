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

public class ProfileServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (SessionUtil.requireLogin(req, resp)) return;

        User user;
        try {
            user = userDao.findById(SessionUtil.currentUserId(req));
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().println("Error loading profile.");
            return;
        }

        resp.setContentType("text/html");
        StringBuilder sb = new StringBuilder();
        sb.append(Page.header("My Profile", req));
        sb.append(req.getParameter("saved") != null ? "<p class=\"success\">Profile updated.</p>" : "");
        sb.append("<form method=\"post\" action=\"/profile/update\">")
          .append("<label>Full name: <input type=\"text\" name=\"fullName\" value=\"")
          .append(HtmlUtil.escape(user.getFullName())).append("\"></label><br>")
          .append("<label>Email: <input type=\"text\" name=\"email\" value=\"")
          .append(HtmlUtil.escape(user.getEmail())).append("\"></label><br>")
          .append("<label>Department: <input type=\"text\" name=\"department\" value=\"")
          .append(HtmlUtil.escape(user.getDepartment())).append("\"></label><br>")
          .append("<label>Bio:<br><textarea name=\"bio\" rows=\"4\" cols=\"50\">")
          .append(HtmlUtil.escape(user.getBio())).append("</textarea></label><br>")
          .append("<button type=\"submit\">Save</button>")
          .append("</form>");
        sb.append(Page.footer());
        resp.getWriter().println(sb);
    }
}
