package com.acme.auth;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
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
        String sql = "SELECT password, full_name FROM users WHERE username = ?";
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, user);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && Passwords.verify(pass, rs.getString(1))) {
                    resp.getWriter().println("{\"login\":\"success\",\"name\":\"" + Util.escape(rs.getString(2))
                            + "\",\"served_by\":\"" + Util.escape(Util.host()) + "\"}");
                } else {
                    resp.setStatus(401);
                    resp.getWriter().println("{\"login\":\"failed\"}");
                }
            }
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().println("{\"error\":\"" + Util.escape(e.getClass().getSimpleName() + ": " + e.getMessage()) + "\"}");
        }
    }
}
