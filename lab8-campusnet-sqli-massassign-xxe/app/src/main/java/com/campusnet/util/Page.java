package com.campusnet.util;

import javax.servlet.http.HttpServletRequest;

/** Tiny HTML shell so every servlet doesn't repeat the same header/nav/footer boilerplate. */
public class Page {

    public static String header(String title, HttpServletRequest req) {
        String username = SessionUtil.currentUsername(req);
        String role = SessionUtil.currentRole(req);

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><title>CampusNet - ")
          .append(HtmlUtil.escape(title))
          .append("</title><link rel=\"stylesheet\" href=\"/static/style.css\"></head><body>");
        sb.append("<header><a class=\"brand\" href=\"/dashboard\">CampusNet</a>");
        sb.append("<a href=\"/catalog\">Course Catalog</a>");
        if (username != null) {
            sb.append("<a href=\"/inbox\">Inbox</a>");
            sb.append("<a href=\"/profile\">My Profile</a>");
            if ("student".equals(role)) {
                sb.append("<a href=\"/grades/my\">My Grades</a>");
            }
            if ("professor".equals(role)) {
                sb.append("<a href=\"/professor/courses\">My Courses</a>");
            }
            if ("admin".equals(role)) {
                sb.append("<a href=\"/admin\">Admin</a>");
            }
            sb.append("<span>Signed in as ").append(HtmlUtil.escape(username))
              .append(" <span class=\"tag\">").append(HtmlUtil.escape(role)).append("</span></span>");
            sb.append("<a href=\"/logout\">Logout</a>");
        } else {
            sb.append("<a href=\"/login\">Login</a>");
            sb.append("<a href=\"/register\">Register</a>");
        }
        sb.append("</header><main>");
        sb.append("<h1>").append(HtmlUtil.escape(title)).append("</h1>");
        return sb.toString();
    }

    public static String footer() {
        return "</main></body></html>";
    }
}
