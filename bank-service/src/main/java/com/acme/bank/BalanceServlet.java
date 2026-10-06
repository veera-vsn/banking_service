package com.acme.bank;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.*;

// GET /balance?user=nani
@WebServlet("/balance")
public class BalanceServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String user = req.getParameter("user");
        resp.setContentType("application/json");
        if (user == null) {
            resp.setStatus(400);
            resp.getWriter().println("{\"error\":\"user parameter required\"}");
            return;
        }
        String sql = "SELECT account_no, balance FROM accounts WHERE username = ?";
        try (Connection c = Db.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, user);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    resp.getWriter().println("{\"user\":\"" + user + "\",\"account\":\"" + rs.getString(1)
                            + "\",\"balance\":" + rs.getBigDecimal(2)
                            + ",\"served_by\":\"" + InetAddress.getLocalHost().getHostName() + "\"}");
                } else {
                    resp.setStatus(404);
                    resp.getWriter().println("{\"error\":\"no account\"}");
                }
            }
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().println("{\"error\":\"" + e.getClass().getSimpleName() + ": " + e.getMessage().replace("\"", "'") + "\"}");
        }
    }
}
