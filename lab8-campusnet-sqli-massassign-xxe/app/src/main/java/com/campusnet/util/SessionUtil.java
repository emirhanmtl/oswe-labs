package com.campusnet.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SessionUtil {

    public static Integer currentUserId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;
        return (Integer) session.getAttribute("userId");
    }

    public static String currentUsername(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;
        return (String) session.getAttribute("username");
    }

    public static String currentRole(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;
        return (String) session.getAttribute("role");
    }

    public static boolean isLoggedIn(HttpServletRequest req) {
        return currentUserId(req) != null;
    }

    /** Returns true (and has already redirected) if the caller should stop processing. */
    public static boolean requireLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isLoggedIn(req)) {
            resp.sendRedirect("/login");
            return true;
        }
        return false;
    }

    /** Returns true (and has already written a 403) if the caller should stop processing. */
    public static boolean requireRole(HttpServletRequest req, HttpServletResponse resp, String... roles) throws IOException {
        if (requireLogin(req, resp)) return true;
        String role = currentRole(req);
        for (String r : roles) {
            if (r.equals(role)) return false;
        }
        resp.setStatus(403);
        resp.setContentType("text/plain");
        resp.getWriter().println("Forbidden: insufficient role.");
        return true;
    }
}
