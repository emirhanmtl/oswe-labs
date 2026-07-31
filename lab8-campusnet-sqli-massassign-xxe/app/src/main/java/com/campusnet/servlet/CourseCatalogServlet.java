package com.campusnet.servlet;

import com.campusnet.dao.CourseDao;
import com.campusnet.model.Course;
import com.campusnet.util.HtmlUtil;
import com.campusnet.util.Page;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Public course catalog - no login required, this is meant to be browsable by
 * prospective students. The department filter is a straightforward parameterized
 * query. (The free-text "search by title or instructor" box on this same page posts
 * to the JSON API at /api/courses/search instead - see CourseSearchApiServlet.)
 */
public class CourseCatalogServlet extends HttpServlet {

    private final CourseDao courseDao = new CourseDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String dept = req.getParameter("dept");
        List<Course> courses;
        try {
            courses = (dept != null && !dept.trim().isEmpty())
                    ? courseDao.findByDepartment(dept)
                    : courseDao.findAll();
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().println("Error loading catalog.");
            return;
        }

        resp.setContentType("text/html");
        StringBuilder sb = new StringBuilder();
        sb.append(Page.header("Course Catalog", req));

        sb.append("<form method=\"get\">")
          .append("<input type=\"text\" name=\"dept\" placeholder=\"Filter by department\" value=\"")
          .append(HtmlUtil.escape(dept == null ? "" : dept)).append("\">")
          .append("<button type=\"submit\">Filter</button>")
          .append("</form>");

        sb.append("<p>Search by course title or instructor name: " +
                "<input type=\"text\" id=\"q\" placeholder=\"e.g. Database, Williams\"> " +
                "<button onclick=\"doSearch()\">Search</button></p>");
        sb.append("<div id=\"results\"></div>");
        sb.append("<script>\n" +
                "function doSearch() {\n" +
                "  var q = document.getElementById('q').value;\n" +
                "  fetch('/api/courses/search?q=' + encodeURIComponent(q))\n" +
                "    .then(function(r) { return r.json(); })\n" +
                "    .then(function(rows) {\n" +
                "      var html = '<table border=\"1\" cellpadding=\"6\"><tr><th>Code</th><th>Title</th><th>Dept</th><th>Instructor</th></tr>';\n" +
                "      rows.forEach(function(c) {\n" +
                "        html += '<tr><td>' + c.code + '</td><td>' + c.title + '</td><td>' + c.department + '</td><td>' + c.instructor + '</td></tr>';\n" +
                "      });\n" +
                "      document.getElementById('results').innerHTML = html + '</table>';\n" +
                "    });\n" +
                "}\n" +
                "</script>");

        sb.append("<h2>All courses</h2>");
        sb.append("<table border=\"1\" cellpadding=\"6\"><tr><th>Code</th><th>Title</th><th>Department</th><th>Credits</th><th>Instructor</th></tr>");
        for (Course c : courses) {
            sb.append("<tr>")
              .append("<td><a href=\"/courses/detail?id=").append(c.getId()).append("\">")
              .append(HtmlUtil.escape(c.getCode())).append("</a></td>")
              .append("<td>").append(HtmlUtil.escape(c.getTitle())).append("</td>")
              .append("<td>").append(HtmlUtil.escape(c.getDepartment())).append("</td>")
              .append("<td>").append(c.getCredits()).append("</td>")
              .append("<td>").append(HtmlUtil.escape(c.getInstructorName())).append("</td>")
              .append("</tr>");
        }
        sb.append("</table>");

        sb.append(Page.footer());
        resp.getWriter().println(sb);
    }
}
