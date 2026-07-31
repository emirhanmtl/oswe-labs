package com.campusnet.servlet;

import com.campusnet.dao.UserDao;
import com.campusnet.util.HtmlUtil;
import com.campusnet.util.Page;
import com.campusnet.util.PasswordUtil;
import com.campusnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Public self-registration. Always creates a plain "student" account - there is no
 * way to pick a different role from this form (role escalation has to happen some
 * other way, later, once you're already signed in).
 */
public class RegisterServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        render(req, resp, null, null);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (SessionUtil.isLoggedIn(req)) {
            resp.sendRedirect("/dashboard");
            return;
        }

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String fullName = req.getParameter("full_name");
        String email = req.getParameter("email");

        if (username == null || username.trim().length() < 3 || password == null || password.length() < 4) {
            render(req, resp, "Username or password too short.", null);
            return;
        }

        try {
            if (userDao.findByUsername(username) != null) {
                render(req, resp, "Username already taken.", null);
                return;
            }
            userDao.insert(username, PasswordUtil.sha1(password), "student",
                    fullName == null || fullName.trim().isEmpty() ? username : fullName, email);
            render(req, resp, null, "Account created. You can log in now.");
        } catch (SQLException e) {
            render(req, resp, "Registration failed.", null);
        }
    }

    private void render(HttpServletRequest req, HttpServletResponse resp, String error, String success) throws IOException {
        resp.setContentType("text/html");
        StringBuilder sb = new StringBuilder();
        sb.append(Page.header("Register", req));
        if (error != null) sb.append("<p class=\"error\">").append(HtmlUtil.escape(error)).append("</p>");
        if (success != null) sb.append("<p class=\"success\">").append(HtmlUtil.escape(success)).append("</p>");
        sb.append("<form method=\"post\">")
          .append("<label>Username: <input type=\"text\" name=\"username\"></label><br>")
          .append("<label>Full name: <input type=\"text\" name=\"full_name\"></label><br>")
          .append("<label>Email: <input type=\"text\" name=\"email\"></label><br>")
          .append("<label>Password: <input type=\"password\" name=\"password\"></label><br>")
          .append("<button type=\"submit\">Register</button>")
          .append("</form>")
          .append("<p><a href=\"/login\">Back to login</a></p>");
        sb.append(Page.footer());
        resp.getWriter().println(sb);
    }
}
