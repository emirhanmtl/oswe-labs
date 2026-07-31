package com.campusnet.servlet;

import com.campusnet.util.Page;
import com.campusnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (SessionUtil.requireLogin(req, resp)) return;

        String role = SessionUtil.currentRole(req);
        resp.setContentType("text/html");
        StringBuilder sb = new StringBuilder();
        sb.append(Page.header("Dashboard", req));

        if ("student".equals(role)) {
            sb.append("<p><a href=\"/catalog\">Browse course catalog</a></p>");
            sb.append("<p><a href=\"/grades/my\">View my grades</a></p>");
        } else if ("professor".equals(role)) {
            sb.append("<p><a href=\"/professor/courses\">My courses & rosters</a></p>");
        } else if ("admin".equals(role)) {
            sb.append("<p><a href=\"/admin\">Admin panel</a></p>");
        }
        sb.append("<p><a href=\"/inbox\">Inbox</a></p>");

        sb.append(Page.footer());
        resp.getWriter().println(sb);
    }
}
