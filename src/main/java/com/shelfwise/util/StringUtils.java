package com.shelfwise.util;

import java.text.Normalizer;

/**
 * Our own string helpers (not the spring one).
 */
public class StringUtils {

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * "Science Fiction & Fantasy" -> "science-fiction-fantasy"
     */
    public static String slugify(String input) {
        if (input == null) {
            return null;
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return normalized.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-");
    }

    public static String truncate(String s, int max) {
        if (s == null || s.length() <= max) {
            return s;
        }
        return s.substring(0, max) + "...";
    }
}
