package com.shelfwise.dto;

import java.math.BigDecimal;

import com.shelfwise.model.CopyCondition;

public class CreateCopyRequest {

    private Long bookId;
    private Long branchId;
    private CopyCondition condition;
    private BigDecimal price;
    private String notes;

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public CopyCondition getCondition() {
        return condition;
    }

    public void setCondition(CopyCondition condition) {
        this.condition = condition;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
