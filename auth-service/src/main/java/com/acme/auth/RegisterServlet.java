package com.acme.auth;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

// POST /register  (form fields: fullName, email, username, password)
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static String clean(String s) { return s == null ? "" : s.trim(); }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String fullName = clean(req.getParameter("fullName"));
        String email    = clean(req.getParameter("email"));
        String username = clean(req.getParameter("username"));
        String password = req.getParameter("password");
        resp.setContentType("application/json");

        String problem = null;
        if (fullName.isEmpty() || fullName.length() > 100)                     problem = "Full name is required";
        else if (!email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+") || email.length() > 100) problem = "Enter a valid email";
        else if (!username.matches("[A-Za-z0-9_]{3,30}"))                      problem = "Username: 3-30 letters, digits or _";
        else if (password == null || password.length() < 6 || password.length() > 100) problem = "Password must be at least 6 characters";
        if (problem != null) {
            resp.setStatus(400);
            resp.getWriter().println("{\"error\":\"" + problem + "\"}");
            return;
        }

        String sql = "INSERT INTO users (username, password, full_name, email) VALUES (?, ?, ?, ?)";
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, Passwords.hash(password));
            ps.setString(3, fullName);
            ps.setString(4, email);
            ps.executeUpdate();
            resp.setStatus(201);
            resp.getWriter().println("{\"register\":\"success\",\"username\":\"" + Util.escape(username)
                    + "\",\"served_by\":\"" + Util.escape(Util.host()) + "\"}");
        } catch (SQLIntegrityConstraintViolationException dup) {
            resp.setStatus(409);
            resp.getWriter().println("{\"error\":\"That username is already taken\"}");
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().println("{\"error\":\"" + Util.escape(e.getClass().getSimpleName() + ": " + e.getMessage()) + "\"}");
        }
    }
}
