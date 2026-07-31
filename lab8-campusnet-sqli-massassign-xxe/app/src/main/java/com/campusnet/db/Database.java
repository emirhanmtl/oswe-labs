package com.campusnet.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static String url() {
        String host = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "db";
        String name = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME") : "campusnet";
        return "jdbc:mysql://" + host + ":3306/" + name
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    private static String user() {
        return System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "campusnet";
    }

    private static String password() {
        return System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "campusnet_pw";
    }

    /** Every caller gets a fresh JDBC connection - simplest thing that works for a small lab app. */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url(), user(), password());
    }
}
