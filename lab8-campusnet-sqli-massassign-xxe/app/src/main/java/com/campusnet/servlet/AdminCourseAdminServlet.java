package com.campusnet.servlet;

import com.campusnet.dao.CourseDao;
import com.campusnet.dao.UserDao;
import com.campusnet.model.Course;
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
import java.util.List;

public class AdminCourseAdminServlet extends HttpServlet {

    private final CourseDao courseDao = new CourseDao();
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (SessionUtil.requireRole(req, resp, "admin")) return;
        render(req, resp, null);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (SessionUtil.requireRole(req, resp, "admin")) return;

        String code = req.getParameter("code");
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String department = req.getParameter("department");
        int instructorId;
        int credits;
        try {
            instructorId = Integer.parseInt(req.getParameter("instructor_id"));
            credits = Integer.parseInt(req.getParameter("credits"));
        } catch (NumberFormatException e) {
            render(req, resp, "Invalid instructor or credits value.");
            return;
        }

        try {
            courseDao.insert(code, title, description, department, credits, instructorId);
        } catch (SQLException e) {
            render(req, resp, "Failed to create course (duplicate code?).");
            return;
        }
        render(req, resp, null);
    }

    private void render(HttpServletRequest req, HttpServletResponse resp, String error) throws IOException {
        List<Course> courses;
        List<User> instructors;
        try {
            courses = courseDao.findAll();
            instructors = userDao.findByRoleIn("professor", "admin");
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().println("Error loading admin course page.");
            return;
        }

        resp.setContentType("text/html");
        StringBuilder sb = new StringBuilder();
        sb.append(Page.header("Manage Courses", req));
        if (error != null) sb.append("<p class=\"error\">").append(HtmlUtil.escape(error)).append("</p>");

        sb.append("<h2>Create course</h2>");
        sb.append("<form method=\"post\">")
          .append("<label>Code: <input type=\"text\" name=\"code\"></label><br>")
          .append("<label>Title: <input type=\"text\" name=\"title\"></label><br>")
          .append("<label>Description: <input type=\"text\" name=\"description\"></label><br>")
          .append("<label>Department: <input type=\"text\" name=\"department\"></label><br>")
          .append("<label>Credits: <input type=\"text\" name=\"credits\" value=\"3\"></label><br>")
          .append("<label>Instructor: <select name=\"instructor_id\">");
        for (User u : instructors) {
            sb.append("<option value=\"").append(u.getId()).append("\">")
              .append(HtmlUtil.escape(u.getFullName())).append("</option>");
        }
        sb.append("</select></label><br>")
          .append("<button type=\"submit\">Create</button></form>");

        sb.append("<h2>Existing courses</h2>");
        sb.append("<table border=\"1\" cellpadding=\"6\"><tr><th>Code</th><th>Title</th><th>Department</th><th>Instructor</th></tr>");
        for (Course c : courses) {
            sb.append("<tr>")
              .append("<td>").append(HtmlUtil.escape(c.getCode())).append("</td>")
              .append("<td>").append(HtmlUtil.escape(c.getTitle())).append("</td>")
              .append("<td>").append(HtmlUtil.escape(c.getDepartment())).append("</td>")
              .append("<td>").append(HtmlUtil.escape(c.getInstructorName())).append("</td>")
              .append("</tr>");
        }
        sb.append("</table>");
        sb.append(Page.footer());
        resp.getWriter().println(sb);
    }
}
