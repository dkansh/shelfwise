package com.shelfwise.fines;

import java.math.BigDecimal;

public class PaymentRequest {

    private BigDecimal amount;
    private PaymentMethod method;
    private String reference;

    public PaymentRequest() {
    }

    public PaymentRequest(BigDecimal amount, PaymentMethod method, String reference) {
        this.amount = amount;
        this.method = method;
        this.reference = reference;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
