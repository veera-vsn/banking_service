package com.acme.bank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {
    // All settings come from environment variables, so the same image works everywhere
    static final String HOST = System.getenv().getOrDefault("DB_HOST", "localhost");
    static final String PORT = System.getenv().getOrDefault("DB_PORT", "3306");
    static final String NAME = System.getenv().getOrDefault("DB_NAME", "bankdb");
    static final String USER = System.getenv().getOrDefault("DB_USER", "root");
    static final String PASS = System.getenv().getOrDefault("DB_PASSWORD", "");

    public static Connection get() throws SQLException {
        String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + NAME
                + "?useSSL=false&allowPublicKeyRetrieval=true&connectTimeout=3000";
        return DriverManager.getConnection(url, USER, PASS);
    }
}
