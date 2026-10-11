package com.ebookstore.dto;

public class PaymentConfirmResponse {

    private Long paymentId;
    private String status;
    private String transactionRef;
    private String message;

    public PaymentConfirmResponse() {}

    public PaymentConfirmResponse(Long paymentId, String status, String transactionRef, String message) {
        this.paymentId = paymentId;
        this.status = status;
        this.transactionRef = transactionRef;
        this.message = message;
    }

    public Long getPaymentId() { return paymentId; }

    public String getStatus() { return status; }

    public String getTransactionRef() { return transactionRef; }

    public String getMessage() { return message; }
}
