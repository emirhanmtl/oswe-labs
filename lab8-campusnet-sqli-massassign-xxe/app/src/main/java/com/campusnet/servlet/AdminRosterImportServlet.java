package com.campusnet.servlet;

import com.campusnet.dao.CourseDao;
import com.campusnet.dao.EnrollmentDao;
import com.campusnet.dao.UserDao;
import com.campusnet.model.User;
import com.campusnet.util.HtmlUtil;
import com.campusnet.util.Page;
import com.campusnet.util.PasswordUtil;
import com.campusnet.util.SessionUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Bulk-enrolls a batch of students onto a course from an uploaded roster XML
 * document - a lot faster than adding each student to a course by hand when a whole
 * section transfers in from another campus system. Admin-only.
 *
 * The parsed roster is always shown back as a preview table before anything is
 * written to the database, so admins can catch typos in the file before confirming.
 */
public class AdminRosterImportServlet extends HttpServlet {

    private final CourseDao courseDao = new CourseDao();
    private final UserDao userDao = new UserDao();
    private final EnrollmentDao enrollmentDao = new EnrollmentDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (SessionUtil.requireRole(req, resp, "admin")) return;
        render(req, resp, null, null, null, null);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (SessionUtil.requireRole(req, resp, "admin")) return;

        String xml = req.getParameter("roster_xml");
        if (xml == null || xml.trim().isEmpty()) {
            render(req, resp, "Paste a roster XML document first.", null, null, null);
            return;
        }

        String courseCode;
        List<String[]> parsed;
        try {
            // Parses whatever roster XML the admin pastes in. No SAX/StAX streaming
            // trickery here - it's a small file, plain DOM is simplest.
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

            courseCode = textOf(doc.getElementsByTagName("course_code"));
            NodeList studentNodes = doc.getElementsByTagName("student");

            parsed = new ArrayList<>();
            for (int i = 0; i < studentNodes.getLength(); i++) {
                Element studentEl = (Element) studentNodes.item(i);
                String username = textOf(studentEl.getElementsByTagName("username"));
                String fullName = textOf(studentEl.getElementsByTagName("full_name"));
                parsed.add(new String[] { username, fullName });
            }
        } catch (Exception e) {
            render(req, resp, "Failed to parse roster XML: " + e.getMessage(), null, null, null);
            return;
        }

        String success = null;
        if ("1".equals(req.getParameter("confirm"))) {
            success = commitImport(courseCode, parsed);
        }

        render(req, resp, null, courseCode, parsed, success);
    }

    private String commitImport(String courseCode, List<String[]> parsed) throws IOException {
        try {
            Integer courseId = courseDao.findIdByCode(courseCode);
            if (courseId == null) {
                return "Course code " + courseCode + " not found - nothing imported.";
            }
            for (String[] s : parsed) {
                String username = s[0];
                String fullName = s[1];
                User existing = userDao.findByUsername(username);
                int studentId;
                if (existing != null) {
                    studentId = existing.getId();
                } else {
                    userDao.insert(username, PasswordUtil.sha1("changeme123"), "student", fullName, null);
                    studentId = userDao.findByUsername(username).getId();
                }
                enrollmentDao.enrollIfAbsent(studentId, courseId);
            }
            return "Imported " + parsed.size() + " student(s) into " + courseCode + ".";
        } catch (Exception e) {
            return "Import failed: " + e.getMessage();
        }
    }

    private String textOf(NodeList list) {
        if (list.getLength() == 0) return "";
        String text = list.item(0).getTextContent();
        return text == null ? "" : text;
    }

    private void render(HttpServletRequest req, HttpServletResponse resp, String error,
                         String courseCode, List<String[]> parsed, String success) throws IOException {
        resp.setContentType("text/html");
        StringBuilder sb = new StringBuilder();
        sb.append(Page.header("Import Course Roster", req));
        sb.append("<p>Paste a roster XML export from the old campus system to bulk-enroll students.</p>");
        if (error != null) sb.append("<p class=\"error\">").append(HtmlUtil.escape(error)).append("</p>");
        if (success != null) sb.append("<p class=\"success\">").append(HtmlUtil.escape(success)).append("</p>");

        sb.append("<form method=\"post\">")
          .append("<label>Roster XML:<br><textarea name=\"roster_xml\" rows=\"10\" cols=\"80\" placeholder=\"")
          .append("&lt;roster&gt;&lt;course_code&gt;CS101&lt;/course_code&gt;&lt;student&gt;...&lt;/student&gt;&lt;/roster&gt;")
          .append("\"></textarea></label><br>")
          .append("<button type=\"submit\" name=\"action\" value=\"preview\">Preview</button>")
          .append("</form>");

        if (parsed != null) {
            sb.append("<h2>Preview - ").append(HtmlUtil.escape(courseCode)).append("</h2>");
            sb.append("<table border=\"1\" cellpadding=\"6\"><tr><th>Username</th><th>Full name</th></tr>");
            for (String[] s : parsed) {
                sb.append("<tr><td>").append(HtmlUtil.escape(s[0])).append("</td>")
                  .append("<td>").append(HtmlUtil.escape(s[1])).append("</td></tr>");
            }
            sb.append("</table>");
            sb.append("<form method=\"post\">")
              .append("<input type=\"hidden\" name=\"roster_xml\" value=\"").append(HtmlUtil.escape(req.getParameter("roster_xml"))).append("\">")
              .append("<input type=\"hidden\" name=\"confirm\" value=\"1\">")
              .append("<button type=\"submit\">Confirm Import</button>")
              .append("</form>");
        }

        sb.append(Page.footer());
        resp.getWriter().println(sb);
    }
}
