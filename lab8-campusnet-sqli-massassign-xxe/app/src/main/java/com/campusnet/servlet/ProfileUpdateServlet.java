package com.campusnet.servlet;

import com.campusnet.dao.UserDao;
import com.campusnet.model.User;
import com.campusnet.util.SessionUtil;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

/**
 * "Flexible" self-service profile editor: whatever bean property a submitted field
 * name matches gets bound straight onto the User object via reflection, so adding a
 * new self-service field later (title, timezone, ...) never needs a servlet change -
 * just add the &lt;input&gt; to the form. The form itself only renders fullName/email/
 * department/bio inputs, but BeanUtils.populate() binds every request parameter whose
 * name matches a bean property, not just the ones the form happens to render.
 */
public class ProfileUpdateServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (SessionUtil.requireLogin(req, resp)) return;

        try {
            User user = userDao.findById(SessionUtil.currentUserId(req));
            if (user == null) {
                resp.setStatus(404);
                resp.getWriter().println("User not found.");
                return;
            }

            BeanUtils.populate(user, req.getParameterMap());

            userDao.update(user);

            // Role can change as a result of this update (e.g. a promotion), so keep
            // the session in sync with whatever ended up in the database.
            HttpSession session = req.getSession(true);
            session.setAttribute("role", user.getRole());

            resp.sendRedirect("/profile?saved=1");
        } catch (SQLException e) {
            resp.setStatus(500);
            resp.getWriter().println("Update failed.");
        } catch (ReflectiveOperationException e) {
            resp.setStatus(400);
            resp.getWriter().println("Invalid profile fields.");
        }
    }
}
