package org.example.gmsnql;

import java.sql.*;

public class RealDatabaseService {
    private final String jdbcUrl;
    private final String user;
    private final String password;

    public RealDatabaseService(String jdbcUrl, String user, String password) {
        this.jdbcUrl = jdbcUrl;
        this.user = user;
        this.password = password;
    }

    /**
     * Executes the SQL query on real DB and returns the ResultSet.
     * Caller is responsible for closing the ResultSet and connection.
     */
    public ResultSet executeQueryOrReturnNull(String sql) throws SQLException {
        Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
        Statement stmt = conn.createStatement();

        boolean hasResultSet = stmt.execute(sql);

        if (hasResultSet) {
            return stmt.getResultSet();
        } else {
            // No ResultSet produced, close resources and return null
            stmt.close();
            conn.close();
            return null;
        }
//        return stmt.executeQuery(sql);
        // Caller must close ResultSet.getStatement().getConnection() after use.
    }
}
