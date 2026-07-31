package com.campusnet.servlet;

import com.campusnet.util.Page;
import com.campusnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminDashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (SessionUtil.requireRole(req, resp, "admin")) return;

        resp.setContentType("text/html");
        StringBuilder sb = new StringBuilder();
        sb.append(Page.header("Admin Panel", req));
        sb.append("<p><a href=\"/admin/users\">Manage users</a></p>");
        sb.append("<p><a href=\"/admin/courses\">Manage courses</a></p>");
        sb.append("<p><a href=\"/admin/roster/import\">Import course roster from XML</a></p>");
        sb.append(Page.footer());
        resp.getWriter().println(sb);
    }
}
