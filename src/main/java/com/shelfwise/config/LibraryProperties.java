package com.shelfwise.config;

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

    public String getOverdueCron() {
        return overdueCron;
    }

    public void setOverdueCron(String overdueCron) {
        this.overdueCron = overdueCron;
    }
}
