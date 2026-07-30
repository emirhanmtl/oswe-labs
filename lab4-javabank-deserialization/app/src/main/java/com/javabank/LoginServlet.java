package com.javabank;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Base64;

public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        resp.setContentType("text/plain");

        if (!Users.check(username, password)) {
            resp.setStatus(401);
            resp.getWriter().println("Invalid credentials");
            return;
        }

        UserToken token = new UserToken(username, Users.isAdmin(username));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(token);
        }
        String cookieValue = Base64.getEncoder().encodeToString(bos.toByteArray());

        Cookie cookie = new Cookie("rememberMe", cookieValue);
        cookie.setPath("/");
        resp.addCookie(cookie);
        resp.getWriter().println("Login successful. rememberMe cookie set - GET /account to view your profile.");
    }
}
