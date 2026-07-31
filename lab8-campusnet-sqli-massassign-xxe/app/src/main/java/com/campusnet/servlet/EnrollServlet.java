package com.campusnet.servlet;

import com.campusnet.dao.EnrollmentDao;
import com.campusnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class EnrollServlet extends HttpServlet {

    private final EnrollmentDao enrollmentDao = new EnrollmentDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (SessionUtil.requireRole(req, resp, "student")) return;

        int courseId;
        try {
            courseId = Integer.parseInt(req.getParameter("course_id"));
        } catch (NumberFormatException e) {
            resp.setStatus(400);
            resp.getWriter().println("Invalid course id.");
            return;
        }

        try {
            if (!enrollmentDao.isEnrolled(SessionUtil.currentUserId(req), courseId)) {
                enrollmentDao.enroll(SessionUtil.currentUserId(req), courseId);
            }
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().println("Enrollment failed.");
            return;
        }
        resp.sendRedirect("/courses/detail?id=" + courseId);
    }
}
