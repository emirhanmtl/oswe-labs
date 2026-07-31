package com.campusnet.servlet;

import com.campusnet.dao.AnnouncementDao;
import com.campusnet.dao.CourseDao;
import com.campusnet.model.Course;
import com.campusnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class AnnouncementCreateServlet extends HttpServlet {

    private final AnnouncementDao announcementDao = new AnnouncementDao();
    private final CourseDao courseDao = new CourseDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (SessionUtil.requireRole(req, resp, "professor", "admin")) return;

        int courseId;
        try {
            courseId = Integer.parseInt(req.getParameter("course_id"));
        } catch (NumberFormatException e) {
            resp.setStatus(400);
            resp.getWriter().println("Invalid course id.");
            return;
        }
        String title = req.getParameter("title");
        String body = req.getParameter("body");
        if (title == null || title.trim().isEmpty() || body == null || body.trim().isEmpty()) {
            resp.sendRedirect("/professor/courses");
            return;
        }

        try {
            Course course = courseDao.findById(courseId);
            String role = SessionUtil.currentRole(req);
            if (course == null || ("professor".equals(role) && course.getInstructorId() != SessionUtil.currentUserId(req))) {
                resp.setStatus(403);
                resp.getWriter().println("Forbidden: not your course.");
                return;
            }
            announcementDao.insert(courseId, SessionUtil.currentUserId(req), title, body);
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().println("Failed to post announcement.");
            return;
        }
        resp.sendRedirect("/professor/courses");
    }
}
