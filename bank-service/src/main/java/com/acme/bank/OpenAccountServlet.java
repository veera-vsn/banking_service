package com.acme.bank;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

// POST /open  (form field: user)  -> opens a bank account for a registered user
@WebServlet("/open")
public class OpenAccountServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String user = req.getParameter("user");
        resp.setContentType("application/json");
        if (user == null || user.isBlank()) {
            resp.setStatus(400);
            resp.getWriter().println("{\"error\":\"user required\"}");
            return;
        }
        try (Connection c = Db.get()) {
            // the user must exist (this reads the shared users table; real microservices would call the auth service instead)
            try (PreparedStatement ps = c.prepareStatement("SELECT 1 FROM users WHERE username = ?")) {
                ps.setString(1, user);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        resp.setStatus(404);
                        resp.getWriter().println("{\"error\":\"unknown user\"}");
                        return;
                    }
                }
            }
            // already has an account? just return it
            try (PreparedStatement ps = c.prepareStatement("SELECT account_no, balance FROM accounts WHERE username = ?")) {
                ps.setString(1, user);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        resp.getWriter().println("{\"open\":\"existing\",\"account\":\"" + Util.escape(rs.getString(1))
                                + "\",\"balance\":" + rs.getBigDecimal(2) + ",\"served_by\":\"" + Util.escape(Util.host()) + "\"}");
                        return;
                    }
                }
            }
            // create the account with a demo welcome balance, then number it from its id
            c.setAutoCommit(false);
            long id;
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO accounts (username, account_no, balance) VALUES (?, 'PENDING', 1000.00)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, user);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    id = keys.getLong(1);
                }
            }
            String accountNo = String.format("ACME%04d", id);
            try (PreparedStatement ps = c.prepareStatement("UPDATE accounts SET account_no = ? WHERE id = ?")) {
                ps.setString(1, accountNo);
                ps.setLong(2, id);
                ps.executeUpdate();
            }
            c.commit();
            resp.setStatus(201);
            resp.getWriter().println("{\"open\":\"success\",\"account\":\"" + accountNo
                    + "\",\"balance\":1000.00,\"served_by\":\"" + Util.escape(Util.host()) + "\"}");
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().println("{\"error\":\"" + Util.escape(e.getClass().getSimpleName() + ": " + e.getMessage()) + "\"}");
        }
    }
}
