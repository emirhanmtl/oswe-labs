package com.campusnet.servlet;

import com.campusnet.dao.AnnouncementDao;
import com.campusnet.dao.CourseDao;
import com.campusnet.model.Announcement;
import com.campusnet.model.Course;
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

public class CourseDetailServlet extends HttpServlet {

    private final CourseDao courseDao = new CourseDao();
    private final AnnouncementDao announcementDao = new AnnouncementDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id;
        try {
            id = Integer.parseInt(req.getParameter("id"));
        } catch (NumberFormatException e) {
            resp.setStatus(400);
            resp.getWriter().println("Invalid course id.");
            return;
        }

        Course course;
        List<Announcement> announcements;
        try {
            course = courseDao.findById(id);
            if (course == null) {
                resp.setStatus(404);
                resp.getWriter().println("Course not found.");
                return;
            }
            announcements = announcementDao.findForCourse(id);
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().println("Error loading course.");
            return;
        }

        resp.setContentType("text/html");
        StringBuilder sb = new StringBuilder();
        sb.append(Page.header(course.getCode() + " - " + course.getTitle(), req));
        sb.append("<p><strong>Department:</strong> ").append(HtmlUtil.escape(course.getDepartment())).append("</p>");
        sb.append("<p><strong>Instructor:</strong> ").append(HtmlUtil.escape(course.getInstructorName())).append("</p>");
        sb.append("<p>").append(HtmlUtil.escape(course.getDescription())).append("</p>");

        if ("student".equals(SessionUtil.currentRole(req))) {
            sb.append("<form method=\"post\" action=\"/courses/enroll\">")
              .append("<input type=\"hidden\" name=\"course_id\" value=\"").append(course.getId()).append("\">")
              .append("<button type=\"submit\">Enroll</button>")
              .append("</form>");
        }

        sb.append("<h2>Announcements</h2>");
        for (Announcement a : announcements) {
            sb.append("<div class=\"card\"><strong>").append(HtmlUtil.escape(a.getTitle())).append("</strong>")
              .append(" <em>by ").append(HtmlUtil.escape(a.getAuthorName())).append("</em>")
              .append("<p>").append(HtmlUtil.escape(a.getBody())).append("</p></div>");
        }

        sb.append(Page.footer());
        resp.getWriter().println(sb);
    }
}
