package com.campusnet;

import com.campusnet.servlet.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Main {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);

        ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.SESSIONS);
        ctx.setContextPath("/");

        ctx.addServlet(new ServletHolder(new HealthServlet()), "/health");

        ctx.addServlet(new ServletHolder(new HomeServlet()), "/");
        ctx.addServlet(new ServletHolder(new RegisterServlet()), "/register");
        ctx.addServlet(new ServletHolder(new LoginServlet()), "/login");
        ctx.addServlet(new ServletHolder(new LogoutServlet()), "/logout");
        ctx.addServlet(new ServletHolder(new DashboardServlet()), "/dashboard");

        ctx.addServlet(new ServletHolder(new CourseCatalogServlet()), "/catalog");
        ctx.addServlet(new ServletHolder(new CourseSearchApiServlet()), "/api/courses/search");
        ctx.addServlet(new ServletHolder(new CourseDetailServlet()), "/courses/detail");
        ctx.addServlet(new ServletHolder(new EnrollServlet()), "/courses/enroll");

        ctx.addServlet(new ServletHolder(new MyGradesServlet()), "/grades/my");

        ctx.addServlet(new ServletHolder(new ProfileServlet()), "/profile");
        ctx.addServlet(new ServletHolder(new ProfileUpdateServlet()), "/profile/update");

        ctx.addServlet(new ServletHolder(new ProfessorCoursesServlet()), "/professor/courses");
        ctx.addServlet(new ServletHolder(new ProfessorGradeEntryServlet()), "/professor/grades");

        ctx.addServlet(new ServletHolder(new AdminDashboardServlet()), "/admin");
        ctx.addServlet(new ServletHolder(new AdminUserListServlet()), "/admin/users");
        ctx.addServlet(new ServletHolder(new AdminCourseAdminServlet()), "/admin/courses");
        ctx.addServlet(new ServletHolder(new AdminRosterImportServlet()), "/admin/roster/import");

        ctx.addServlet(new ServletHolder(new AnnouncementCreateServlet()), "/announcements/create");

        ctx.addServlet(new ServletHolder(new InboxServlet()), "/inbox");
        ctx.addServlet(new ServletHolder(new MessageSendServlet()), "/messages/send");

        ServletHolder staticHolder = new ServletHolder("static", DefaultServlet.class);
        staticHolder.setInitParameter("resourceBase", "/app/static");
        staticHolder.setInitParameter("dirAllowed", "false");
        ctx.addServlet(staticHolder, "/static/*");

        server.setHandler(ctx);
        server.start();
        System.out.println("campusnet listening on :8080");
        server.join();
    }
}
