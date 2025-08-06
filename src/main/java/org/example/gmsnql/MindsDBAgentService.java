package org.example.gmsnql;

import java.sql.*;

public class MindsDBAgentService {

    private final String jdbcUrl;
    private final String user;
    private final String password;

    public MindsDBAgentService(String jdbcUrl, String user, String password) {
        this.jdbcUrl = jdbcUrl;
        this.user = user;
        this.password = password;
    }

    public String getGeneratedSql(String question) throws SQLException {
        String sql = "SELECT answer FROM gms_ai_agent WHERE question = ?";

        try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, question);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("answer");
                }
                else
                    throw new RuntimeException("No Answer (SQL) returned from MindsDB agent");
            }
        }
    }
}
