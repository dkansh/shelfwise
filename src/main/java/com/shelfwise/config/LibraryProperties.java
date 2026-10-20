package com.shelfwise.config;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Library rules that can be changed without touching the code.
 * Bound from the {@code shelfwise.*} keys in application.yml.
 */
@ConfigurationProperties(prefix = "shelfwise")
public class LibraryProperties {

    /** How many books a member can have checked out at the same time. */
    private int maxLoansPerMember = 5;

    /** Number of days a member can keep a book. */
    private int loanPeriodDays = 14;

    /** Fine charged for each day a book is late. */
    private double finePerDay = 0.25;

    /** Max number of times a loan can be renewed. */
    private int maxRenewals = 2;

    /** Members who owe more than this can't borrow anything. */
    private BigDecimal maxOutstandingFine = new BigDecimal("10.00");

    /** How long a reserved book is kept at the desk once it's ready. */
    private int reservationHoldDays = 3;

    /** Days the library is closed, as yyyy-MM-dd strings. */
    private List<String> holidays = new ArrayList<>();

    private String overdueCron = "0 0 2 * * *";

    public int getMaxLoansPerMember() {
        return maxLoansPerMember;
    }

    public void setMaxLoansPerMember(int maxLoansPerMember) {
        this.maxLoansPerMember = maxLoansPerMember;
    }

    public int getLoanPeriodDays() {
        return loanPeriodDays;
    }

    public void setLoanPeriodDays(int loanPeriodDays) {
        this.loanPeriodDays = loanPeriodDays;
    }

    public double getFinePerDay() {
        return finePerDay;
    }

    public void setFinePerDay(double finePerDay) {
        this.finePerDay = finePerDay;
    }

    public int getMaxRenewals() {
        return maxRenewals;
    }

    public void setMaxRenewals(int maxRenewals) {
        this.maxRenewals = maxRenewals;
    }

    public BigDecimal getMaxOutstandingFine() {
        return maxOutstandingFine;
    }

    public void setMaxOutstandingFine(BigDecimal maxOutstandingFine) {
        this.maxOutstandingFine = maxOutstandingFine;
    }

    public int getReservationHoldDays() {
        return reservationHoldDays;
    }

    public void setReservationHoldDays(int reservationHoldDays) {
        this.reservationHoldDays = reservationHoldDays;
    }

    public List<String> getHolidays() {
        return holidays;
    }

    public void setHolidays(List<String> holidays) {
        this.holidays = holidays;
    }

    public String getOverdueCron() {
        return overdueCron;
    }

    public void setOverdueCron(String overdueCron) {
        this.overdueCron = overdueCron;
    }
}
