package com.acme.auth;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;

// GET /health -> 200 if the app AND the database are reachable, else 503
@WebServlet("/health")
public class HealthServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try (Connection c = Db.get()) {
            resp.getWriter().println("{\"service\":\"auth\",\"status\":\"UP\",\"db\":\"UP\"}");
        } catch (Exception e) {
            resp.setStatus(503);
            resp.getWriter().println("{\"service\":\"auth\",\"status\":\"DOWN\",\"db\":\"" + e.getClass().getSimpleName() + "\"}");
        }
    }
}
