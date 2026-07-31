package com.campusnet.servlet;

import com.campusnet.dao.UserDao;
import com.campusnet.model.User;
import com.campusnet.util.HtmlUtil;
import com.campusnet.util.Page;
import com.campusnet.util.PasswordUtil;
import com.campusnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

public class LoginServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (SessionUtil.isLoggedIn(req)) {
            resp.sendRedirect("/dashboard");
            return;
        }
        render(req, resp, null);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        try {
            User user = userDao.findByUsername(username);
            if (user != null && user.getPasswordHash().equals(PasswordUtil.sha1(password))) {
                HttpSession session = req.getSession(true);
                session.setAttribute("userId", user.getId());
                session.setAttribute("username", user.getUsername());
                session.setAttribute("role", user.getRole());
                resp.sendRedirect("/dashboard");
                return;
            }
        } catch (SQLException e) {
            // fall through to invalid-credentials message
        }
        render(req, resp, "Invalid username or password.");
    }

    private void render(HttpServletRequest req, HttpServletResponse resp, String error) throws IOException {
        resp.setContentType("text/html");
        StringBuilder sb = new StringBuilder();
        sb.append(Page.header("Log In", req));
        if (error != null) sb.append("<p class=\"error\">").append(HtmlUtil.escape(error)).append("</p>");
        sb.append("<form method=\"post\">")
          .append("<label>Username: <input type=\"text\" name=\"username\"></label><br>")
          .append("<label>Password: <input type=\"password\" name=\"password\"></label><br>")
          .append("<button type=\"submit\">Log In</button>")
          .append("</form>")
          .append("<p><a href=\"/register\">Register a student account</a></p>");
        sb.append(Page.footer());
        resp.getWriter().println(sb);
    }
}
