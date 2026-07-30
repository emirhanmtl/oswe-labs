package com.javabank;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Base64;

public class AccountServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");

        String tokenCookie = null;
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("rememberMe".equals(c.getName())) {
                    tokenCookie = c.getValue();
                }
            }
        }

        if (tokenCookie == null) {
            resp.setStatus(401);
            resp.getWriter().println("Not authenticated - log in first or supply a rememberMe cookie.");
            return;
        }

        try {
            byte[] data = Base64.getDecoder().decode(tokenCookie);
            // Session tokens are our own serialized objects, so we trust them
            // enough to deserialize directly.
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
                Object obj = ois.readObject();
                if (obj instanceof UserToken) {
                    UserToken token = (UserToken) obj;
                    resp.getWriter().println("Welcome " + token.username + (token.admin ? " (admin)" : ""));
                } else {
                    resp.getWriter().println("Unrecognized token type: " + obj.getClass().getName());
                }
            }
        } catch (Exception e) {
            resp.setStatus(400);
            resp.getWriter().println("Invalid token: " + e.getMessage());
        }
    }
}
