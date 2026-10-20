package com.shelfwise.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.shelfwise.config.LibraryProperties;

class LibraryCalendarServiceTest {

    private LibraryCalendarService calendar;

    @BeforeEach
    void setUp() {
        LibraryProperties properties = new LibraryProperties();
        properties.setHolidays(List.of("2026-12-25", "2026-12-26"));
        calendar = new LibraryCalendarService(properties);
    }

    @Test
    void closedOnSunday() {
        assertFalse(calendar.isOpen(LocalDate.of(2026, 10, 18)));
    }

    @Test
    void openOnNormalWeekday() {
        assertTrue(calendar.isOpen(LocalDate.of(2026, 10, 20)));
    }

    @Test
    void closedOnHoliday() {
        assertFalse(calendar.isOpen(LocalDate.of(2026, 12, 25)));
    }

    @Test
    void nextOpenDay_skipsHolidaysAndSunday() {
        // 25th + 26th are holidays, 27th is a Sunday
        assertEquals(LocalDate.of(2026, 12, 28), calendar.nextOpenDay(LocalDate.of(2026, 12, 25)));
    }

    @Test
    void nextOpenDay_returnsSameDayWhenOpen() {
        LocalDate tuesday = LocalDate.of(2026, 10, 20);
        assertEquals(tuesday, calendar.nextOpenDay(tuesday));
    }
}
