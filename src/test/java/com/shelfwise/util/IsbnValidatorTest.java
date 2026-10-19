package com.shelfwise.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class IsbnValidatorTest {

    @Test
    void validIsbn13() {
        assertTrue(IsbnValidator.isValid("9780132350884"));
    }

    @Test
    void validIsbn13WithHyphens() {
        assertTrue(IsbnValidator.isValid("978-0-13-235088-4"));
    }

    @Test
    void wrongCheckDigit() {
        assertFalse(IsbnValidator.isValid("9780132350885"));
    }

    @Test
    void nullOrEmpty() {
        assertFalse(IsbnValidator.isValid(null));
        assertFalse(IsbnValidator.isValid(""));
        assertFalse(IsbnValidator.isValid("   "));
    }

    @Test
    void lettersAreNotAllowed() {
        assertFalse(IsbnValidator.isValid("97801323508AB"));
    }
}
