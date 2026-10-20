package com.shelfwise.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.shelfwise.config.LibraryProperties;

/**
 * Knows which days the library is open.
 */
@Service
public class LibraryCalendarService {

    private final Set<LocalDate> holidays = new HashSet<>();

    public LibraryCalendarService(LibraryProperties properties) {
        for (String holiday : properties.getHolidays()) {
            holidays.add(LocalDate.parse(holiday));
        }
    }

    /**
     * The library is closed on Sundays and on public holidays.
     */
    public boolean isOpen(LocalDate date) {
        return date.getDayOfWeek() != DayOfWeek.SUNDAY && !holidays.contains(date);
    }

    /**
     * Returns the given date if the library is open that day, otherwise the next day it is open.
     * Used so due dates never land on a closed day.
     */
    public LocalDate nextOpenDay(LocalDate date) {
        LocalDate day = date;
        while (!isOpen(day)) {
            day = day.plusDays(1);
        }
        return day;
    }

    public int openDaysBetween(LocalDate start, LocalDate end) {
        int count = 0;
        for (LocalDate d = start; d.isBefore(end); d = d.plusDays(1)) {
            if (isOpen(d)) {
                count++;
            }
        }
        return count;
    }
}
