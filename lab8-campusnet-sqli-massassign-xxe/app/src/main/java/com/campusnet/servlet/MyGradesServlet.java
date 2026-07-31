package com.campusnet.servlet;

import com.campusnet.dao.GradeDao;
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

public class MyGradesServlet extends HttpServlet {

    private final GradeDao gradeDao = new GradeDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (SessionUtil.requireRole(req, resp, "student")) return;

        List<Grade> grades;
        try {
            grades = gradeDao.findForStudent(SessionUtil.currentUserId(req));
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().println("Error loading grades.");
            return;
        }

        resp.setContentType("text/html");
        StringBuilder sb = new StringBuilder();
        sb.append(Page.header("My Grades", req));
        sb.append("<table border=\"1\" cellpadding=\"6\"><tr><th>Course</th><th>Title</th><th>Grade</th></tr>");
        for (Grade g : grades) {
            sb.append("<tr>")
              .append("<td>").append(HtmlUtil.escape(g.getCourseCode())).append("</td>")
              .append("<td>").append(HtmlUtil.escape(g.getCourseTitle())).append("</td>")
              .append("<td>").append(g.getGrade() == null ? "In progress" : HtmlUtil.escape(g.getGrade())).append("</td>")
              .append("</tr>");
        }
        sb.append("</table>");
        sb.append(Page.footer());
        resp.getWriter().println(sb);
    }
}
