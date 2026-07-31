package com.campusnet.servlet;

import com.campusnet.db.Database;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Public (no login required) JSON search API backing the catalog page's free-text
 * search box. Deliberately not gated behind auth - prospective students without an
 * account yet should still be able to search for courses/instructors.
 *
 * Kept as a plain Statement query (built by hand from the prototype) rather than a
 * PreparedStatement because the search spans two different columns (course title
 * OR instructor name) with a single free-text box - a "?" placeholder didn't map
 * cleanly onto that, and nobody revisited it once the feature shipped.
 */
public class CourseSearchApiServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String q = req.getParameter("q");
        if (q == null) q = "";

        String sql = "SELECT c.code AS code, c.title AS title, c.department AS department, " +
                "u.full_name AS instructor_name " +
                "FROM courses c JOIN users u ON u.id = c.instructor_id " +
                "WHERE c.title LIKE '%" + q + "%' OR u.full_name LIKE '%" + q + "%'";

        StringBuilder json = new StringBuilder("[");
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            boolean first = true;
            while (rs.next()) {
                if (!first) json.append(",");
                first = false;
                json.append("{")
                    .append("\"code\":").append(jsonString(rs.getString("code"))).append(",")
                    .append("\"title\":").append(jsonString(rs.getString("title"))).append(",")
                    .append("\"department\":").append(jsonString(rs.getString("department"))).append(",")
                    .append("\"instructor\":").append(jsonString(rs.getString("instructor_name")))
                    .append("}");
            }
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().print("[]");
            return;
        }
        json.append("]");
        resp.getWriter().print(json);
    }

    private String jsonString(String s) {
        if (s == null) return "null";
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ") + "\"";
    }
}
