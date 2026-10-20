package com.shelfwise.util;

public class CsvUtils {

    /**
     * Splits one line of a CSV file into columns.
     */
    public static String[] parseLine(String line) {
        // simple split is good enough for our files
        // TODO: handle quoted values that have commas inside them
        return line.split(",", -1);
    }

    /**
     * Makes a value safe to put in a CSV cell.
     */
    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    public static String toLine(Object... values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(escape(values[i] == null ? null : values[i].toString()));
        }
        return sb.toString();
    }
}
