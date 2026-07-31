package com.campusnet.servlet;

import com.campusnet.dao.CourseDao;
import com.campusnet.dao.GradeDao;
import com.campusnet.model.Course;
import com.campusnet.model.Grade;
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

public class ProfessorCoursesServlet extends HttpServlet {

    private final CourseDao courseDao = new CourseDao();
    private final GradeDao gradeDao = new GradeDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (SessionUtil.requireRole(req, resp, "professor")) return;

        int instructorId = SessionUtil.currentUserId(req);
        List<Course> courses;
        try {
            courses = courseDao.findByInstructor(instructorId);
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().println("Error loading courses.");
            return;
        }

        resp.setContentType("text/html");
        StringBuilder sb = new StringBuilder();
        sb.append(Page.header("My Courses", req));

        for (Course c : courses) {
            sb.append("<div class=\"card\"><h3>").append(HtmlUtil.escape(c.getCode()))
              .append(" - ").append(HtmlUtil.escape(c.getTitle())).append("</h3>");
            sb.append("<form method=\"post\" action=\"/announcements/create\">")
              .append("<input type=\"hidden\" name=\"course_id\" value=\"").append(c.getId()).append("\">")
              .append("Post announcement: <input type=\"text\" name=\"title\" placeholder=\"Title\"> ")
              .append("<input type=\"text\" name=\"body\" placeholder=\"Body\"> ")
              .append("<button type=\"submit\">Post</button></form>");

            sb.append("<h4>Roster</h4><table border=\"1\" cellpadding=\"6\"><tr><th>Student</th><th>Grade</th><th>Update</th></tr>");
            try {
                List<Grade> roster = gradeDao.findForCourse(c.getId());
                for (Grade g : roster) {
                    sb.append("<tr><td>").append(HtmlUtil.escape(g.getStudentName())).append("</td>")
                      .append("<td>").append(g.getGrade() == null ? "-" : HtmlUtil.escape(g.getGrade())).append("</td>")
                      .append("<td><form method=\"post\" action=\"/professor/grades\" style=\"display:inline\">")
                      .append("<input type=\"hidden\" name=\"enrollment_id\" value=\"").append(g.getEnrollmentId()).append("\">")
                      .append("<input type=\"text\" name=\"grade\" size=\"4\" placeholder=\"A-\">")
                      .append("<button type=\"submit\">Set</button></form></td></tr>");
                }
            } catch (SQLException e) {
                sb.append("<tr><td colspan=\"3\">Error loading roster.</td></tr>");
            }
            sb.append("</table></div>");
        }

        sb.append(Page.footer());
        resp.getWriter().println(sb);
    }
}
