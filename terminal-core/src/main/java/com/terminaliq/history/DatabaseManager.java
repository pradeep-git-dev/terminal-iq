package com.terminaliq.history;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private final String dbUrl;

    public DatabaseManager() {
        this("jdbc:sqlite:terminal-iq.db");
    }

    public DatabaseManager(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }

    public void initialize() {
        try {
            Class.forName("org.sqlite.JDBC");
            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement()) {
                String sql = "CREATE TABLE IF NOT EXISTS command_history (" +
                             "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                             "command TEXT NOT NULL, " +
                             "working_directory TEXT NOT NULL, " +
                             "exit_code INTEGER, " +
                             "duration_ms INTEGER, " +
                             "executed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                             ");";
                stmt.execute(sql);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Database driver error: SQLite JDBC driver not found. History persistence will be disabled.");
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage() + ". History persistence will be disabled.");
        }
    }
}
