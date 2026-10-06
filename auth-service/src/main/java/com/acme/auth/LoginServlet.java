package com.acme.auth;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.*;

// POST /login  (form fields: username, password)
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String user = req.getParameter("username");
        String pass = req.getParameter("password");
        resp.setContentType("application/json");

        if (user == null || pass == null) {
            resp.setStatus(400);
            resp.getWriter().println("{\"error\":\"username and password required\"}");
            return;
        }
        String sql = "SELECT full_name FROM users WHERE username = ? AND password = ?";
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, user);
            ps.setString(2, pass);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    resp.getWriter().println("{\"login\":\"success\",\"name\":\"" + rs.getString(1)
                            + "\",\"served_by\":\"" + InetAddress.getLocalHost().getHostName() + "\"}");
                } else {
                    resp.setStatus(401);
                    resp.getWriter().println("{\"login\":\"failed\"}");
                }
            }
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().println("{\"error\":\"" + e.getClass().getSimpleName() + ": " + e.getMessage().replace("\"", "'") + "\"}");
        }
    }
}
