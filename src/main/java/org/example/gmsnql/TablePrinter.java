//package org.example.gmsnql;
//
//import de.vandermeer.asciitable.AsciiTable;
//
//import java.sql.ResultSet;
//import java.sql.ResultSetMetaData;
//import java.util.ArrayList;
//import java.util.List;
//
//public class TablePrinter {
//
//    private static String centerString(int width, String s) {
//        if (s == null) s = "";
//        int padding = width - s.length();
//
//        if (padding <= 0)
//        {
//            return s;
//        }
//        int padLeft = padding / 2;
//        int padRight = padding - padLeft;
//        return " ".repeat(padLeft) + s + " ".repeat(padRight);
//    }
//
//    public static void printResultSet(ResultSet rs) throws Exception {
//        ResultSetMetaData md = rs.getMetaData();
//        int cols = md.getColumnCount();
//        AsciiTable at = new AsciiTable();
//
//        int[] maxWidths = new int[cols];
//        for (int i = 1; i <= cols; i++) {
//            maxWidths[i - 1] = md.getColumnName(i).length();
//        }
//
////        Collect rows in memory to compute column widths
//        List<String[]> rows = new ArrayList<>();
//        while (rs.next()) {
//            String[] row = new String[cols];
//            for (int i = 1; i <= cols; i++) {
//                String val = rs.getString(i);
//                if (val == null) val = "";
//                row[i - 1] = val;
//                if (val.length() > maxWidths[i - 1]) {
//                    maxWidths[i - 1] = val.length();
//                }
//            }
//            rows.add(row);
//        }
//
////      Add header with center alignment
//        String[] header = new String[cols];
//        for (int i = 1; i <= cols; i++) {
//            header[i - 1] = centerString(maxWidths[i - 1], md.getColumnName(i));
//        }
//        at.addRule();
//        at.addRow((Object[]) header);
//        at.addRule();
//
////      Add Rows with center alignment
//        for (String[] row : rows) {
//            Object[] centeredRow = new Object[cols];
//            for (int i = 0; i < cols; i++) {
//                centeredRow[i] = centerString(maxWidths[i], row[i]);
//            }
//            at.addRow(centeredRow);
//            at.addRule();
//        }
//
//
//        System.out.println(at.render());
//    }
//}

package org.example.gmsnql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class TablePrinter {

//    Helper method to center-align a string within a given width
    private static String centerString(int width, String s) {
        if (s == null) s = "";
        int padding = width - s.length();

        if (padding <= 0)
        {
            return s;
        }
        int padLeft = padding / 2;
        int padRight = padding - padLeft;
        return " ".repeat(padLeft) + s + " ".repeat(padRight);
    }

//    Build a Horizontal border line, e.g. +--------+--------+--------+
    private static String buildBorderLine(int[] colWidths) {
        StringBuilder sb = new StringBuilder("+");
        for (int w : colWidths) {
            sb.append("-".repeat(w + 2)); // +2 for padding spaces left and right
            sb.append("+");
        }
        return sb.toString();
    }

//    Print the entire ResultSet as a centered ASCII table
    public static void printResultSet(ResultSet rs) throws Exception {
        ResultSetMetaData md = rs.getMetaData();
        int cols = md.getColumnCount();

//        1. Determine max width per column (header vs data)
        int[] colWidths = new int[cols];
        for (int i = 0; i < cols; i++) {
            colWidths[i] = md.getColumnName(i + 1).length();
        }

//        Read All Rows into memory to calculate widths
        List<String[]> rows = new ArrayList<>();
        while (rs.next()) {
            String[] row = new String[cols];
            for (int i = 0; i < cols; i++) {
                String val = rs.getString(i + 1);
                val = (val == null) ? "" : val;
                row[i] = val;
                if (val.length() > colWidths[i]) {
                    colWidths[i] = val.length();
                }
            }
            rows.add(row);
        }

        if (rows.isEmpty()) {
            System.out.println("No results found.");
            return;
        }

//        2. Print the table

//        Print top border
        System.out.println(buildBorderLine(colWidths));

//        Print header row
        StringBuilder headerLine = new StringBuilder("|");
        for (int i = 0; i < cols; i++) {
            String header = md.getColumnName(i + 1);
            headerLine.append(" ")
                    .append(centerString(colWidths[i], header))
                    .append(" |");
        }
        System.out.println(headerLine.toString());

//        Print Header-botton border
        System.out.println(buildBorderLine(colWidths));

//        Print data rows
        for (String[] row : rows) {
            StringBuilder rowLine = new StringBuilder("|");
            for (int i = 0; i < cols; i++) {
                rowLine.append(" ")
                        .append(centerString(colWidths[i], row[i]))
                        .append(" |");
            }
            System.out.println(rowLine.toString());
        }

//        Print bottom border
        System.out.println(buildBorderLine(colWidths));
    }
}
