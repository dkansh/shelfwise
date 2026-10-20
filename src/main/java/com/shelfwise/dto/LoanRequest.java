package com.shelfwise.dto;

public class LoanRequest {

    private Long memberId;
    private Long bookId;

    // optional, the barcode of the physical copy scanned at the desk
    private String copyBarcode;

    public LoanRequest() {
    }

    public LoanRequest(Long memberId, Long bookId) {
        this.memberId = memberId;
        this.bookId = bookId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getCopyBarcode() {
        return copyBarcode;
    }

    public void setCopyBarcode(String copyBarcode) {
        this.copyBarcode = copyBarcode;
    }
}
