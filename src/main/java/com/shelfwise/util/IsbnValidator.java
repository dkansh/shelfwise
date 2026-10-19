package com.shelfwise.util;

/**
 * Helper for checking ISBN numbers.
 */
public final class IsbnValidator {

    private IsbnValidator() {
    }

    /**
     * Checks if the given string is a valid ISBN. Hyphens and spaces are ignored,
     * so "978-0-13-235088-4" and "9780132350884" are both fine.
     *
     * @param isbn the isbn to check
     * @return true if the isbn is valid
     */
    public static boolean isValid(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            return false;
        }

        String cleaned = isbn.replace("-", "").replace(" ", "");

        if (cleaned.length() == 10) {
            // TODO: support ISBN-10 (lots of older books only have this). rejecting them for now
            return false;
        }

        if (cleaned.length() != 13) {
            return false;
        }

        int sum = 0;
        for (int i = 0; i < 12; i++) {
            char c = cleaned.charAt(i);
            if (!Character.isDigit(c)) {
                return false;
            }
            int digit = c - '0';
            sum += (i % 2 == 0) ? digit : digit * 3;
        }

        int checkDigit = (10 - (sum % 10)) % 10;
        return checkDigit == cleaned.charAt(12) - '0';
    }
}
