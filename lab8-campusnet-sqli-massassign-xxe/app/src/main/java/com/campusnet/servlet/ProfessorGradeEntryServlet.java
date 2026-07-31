package com.campusnet.servlet;

import com.campusnet.dao.CourseDao;
import com.campusnet.dao.GradeDao;
import com.campusnet.model.Course;
import com.campusnet.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class ProfessorGradeEntryServlet extends HttpServlet {

    private final GradeDao gradeDao = new GradeDao();
    private final CourseDao courseDao = new CourseDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (SessionUtil.requireRole(req, resp, "professor")) return;

        int enrollmentId;
        try {
            enrollmentId = Integer.parseInt(req.getParameter("enrollment_id"));
        } catch (NumberFormatException e) {
            resp.setStatus(400);
            resp.getWriter().println("Invalid enrollment id.");
            return;
        }
        String grade = req.getParameter("grade");

        try {
            Integer courseId = gradeDao.findCourseIdForEnrollment(enrollmentId);
            if (courseId == null) {
                resp.setStatus(404);
                resp.getWriter().println("Enrollment not found.");
                return;
            }
            Course course = courseDao.findById(courseId);
            if (course == null || course.getInstructorId() != SessionUtil.currentUserId(req)) {
                resp.setStatus(403);
                resp.getWriter().println("Forbidden: not your course.");
                return;
            }
            gradeDao.ensureGradeRow(enrollmentId);
            gradeDao.setGrade(enrollmentId, grade);
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().println("Failed to set grade.");
            return;
        }
        resp.sendRedirect("/professor/courses");
    }
}
