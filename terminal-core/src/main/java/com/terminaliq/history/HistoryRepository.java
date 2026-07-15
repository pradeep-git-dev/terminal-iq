package com.terminaliq.history;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HistoryRepository {
    private final DatabaseManager dbManager;

    public HistoryRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void save(HistoryEntry entry) {
        String sql = "INSERT INTO command_history(command, working_directory, exit_code, duration_ms) VALUES(?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, entry.getCommand());
            pstmt.setString(2, entry.getWorkingDirectory());
            pstmt.setInt(3, entry.getExitCode());
            pstmt.setLong(4, entry.getDurationMs());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving command to history: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected database error: " + e.getMessage());
        }
    }

    public List<HistoryEntry> getHistory(int limit) {
        List<HistoryEntry> list = new ArrayList<>();
        String sql;
        if (limit > 0) {
            sql = "SELECT id, command, working_directory, exit_code, duration_ms, executed_at " +
                  "FROM (SELECT id, command, working_directory, exit_code, duration_ms, executed_at " +
                  "      FROM command_history ORDER BY id DESC LIMIT ?) " +
                  "ORDER BY id ASC";
        } else {
            sql = "SELECT id, command, working_directory, exit_code, duration_ms, executed_at " +
                  "FROM command_history ORDER BY id ASC";
        }

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (limit > 0) {
                pstmt.setInt(1, limit);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    HistoryEntry entry = new HistoryEntry(
                        rs.getInt("id"),
                        rs.getString("command"),
                        rs.getString("working_directory"),
                        rs.getInt("exit_code"),
                        rs.getLong("duration_ms"),
                        rs.getString("executed_at")
                    );
                    list.add(entry);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving command history: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected database error: " + e.getMessage());
        }
        return list;
    }
}
