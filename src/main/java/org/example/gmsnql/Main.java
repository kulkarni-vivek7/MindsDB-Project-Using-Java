package org.example.gmsnql;

import java.io.FileInputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("src/main/resources/config.properties")) {
            props.load(fis);
        }
        catch (Exception e) {
            System.err.println("Failed to load config.properties");
            e.printStackTrace();
            return; // Exit if config loading fails
        }

        // MindsDB agent connection info (MySQL API, typically port 47335)
        String mindsdbJdbcUrl = props.getProperty("mindsdb.jdbc.url"); // e.g. "jdbc:mysql://localhost:47335/mindsdb"
        String mindsdbUser = props.getProperty("mindsdb.jdbc.user"); // e.g. "mindsdb"
        String mindsdbPassword = props.getProperty("mindsdb.jdbc.password"); // e.g. "mindsdb_password" or empty if no password

        // Your real MySQL database containing actual tables
        String realDbJdbcUrl = props.getProperty("realdb.jdbc.url"); // your Local MySQL database URL
        String realDbUser = props.getProperty("realdb.jdbc.user"); // Your Local MySQL username
        String realDbPassword = props.getProperty("realdb.jdbc.password"); // Your Local MySQL password

        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Enter Your Question: ");
            String question = sc.nextLine();

            // NEW: Show message while processing
            System.out.println("Generating Answer...\n");

            MindsDBAgentService agentService = new MindsDBAgentService(mindsdbJdbcUrl, mindsdbUser, mindsdbPassword);
            String generatedSqlRaw = agentService.getGeneratedSql(question);
            String generatedSql = cleanGeneratedSql(generatedSqlRaw);

            System.out.println("Generated SQL from MindsDB agent (cleaned):");
            System.out.println(generatedSql);

//            For one line space between the generated SQL and the table output
            System.out.println();

            RealDatabaseService realDatabaseService = new RealDatabaseService(realDbJdbcUrl, realDbUser, realDbPassword);
            try (ResultSet rs = realDatabaseService.executeQueryOrReturnNull(generatedSql)){
                if (rs != null) {
                    try {
                        TablePrinter.printResultSet(rs);
                    }
                    finally {
                        Statement stmt = rs.getStatement();
                        rs.close();
                        stmt.getConnection().close();
                        stmt.close();
                    }
                }
                else
                {
                    System.out.println("Query executed but did not produce a result set (no data to display).");
                }
            }
        } catch (Exception e) {
            System.err.println("Error during querying:");
            e.printStackTrace();
        }
    }

    public static String cleanGeneratedSql(String rawSql) {
        if (rawSql == null) return null;

        String cleaned = rawSql.trim();

//        Remove opening backticks + optional language tag (e.g. ```)
        if (cleaned.startsWith("```")) {
            int firstNewLine = cleaned.indexOf('\n');
            if (firstNewLine > 0) {
                cleaned = cleaned.substring(firstNewLine + 1).trim();
            }
            else
                cleaned = cleaned.substring(3).trim();
        }

//        Remove trailing triple backticks ```
        if (cleaned.endsWith("```")){
            cleaned = cleaned.substring(0, cleaned.length() - 3).trim();
        }

        return cleaned;
    }
}