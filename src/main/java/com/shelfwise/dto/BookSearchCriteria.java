package com.shelfwise.dto;

/**
 * Query params for GET /api/v2/books/search
 */
public class BookSearchCriteria {

    private String q;
    private String author;
    private String category;
    private String language;
    private Boolean availableOnly;
    private Integer yearFrom;
    private Integer yearTo;

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Boolean getAvailableOnly() {
        return availableOnly;
    }

    public void setAvailableOnly(Boolean availableOnly) {
        this.availableOnly = availableOnly;
    }

    public Integer getYearFrom() {
        return yearFrom;
    }

    public void setYearFrom(Integer yearFrom) {
        this.yearFrom = yearFrom;
    }

    public Integer getYearTo() {
        return yearTo;
    }

    public void setYearTo(Integer yearTo) {
        this.yearTo = yearTo;
    }
}
