package com.acme.bank;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/health")
public class HealthServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try (Connection c = Db.get()) {
            resp.getWriter().println("{\"service\":\"bank\",\"status\":\"UP\",\"db\":\"UP\"}");
        } catch (Exception e) {
            resp.setStatus(503);
            resp.getWriter().println("{\"service\":\"bank\",\"status\":\"DOWN\",\"db\":\"" + e.getClass().getSimpleName() + "\"}");
        }
    }
}
