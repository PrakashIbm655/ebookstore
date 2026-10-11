package com.ebookstore.dto;

import java.math.BigDecimal;

public class PaymentInitiateResponse {

    private Long paymentId;
    private BigDecimal amount;
    private Integer giftPointsUsed;
    private BigDecimal finalAmount;
    private String status;
    private String message;

    public PaymentInitiateResponse() {}

    public PaymentInitiateResponse(Long paymentId, BigDecimal amount, Integer giftPointsUsed,
                                   BigDecimal finalAmount, String status, String message) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.giftPointsUsed = giftPointsUsed;
        this.finalAmount = finalAmount;
        this.status = status;
        this.message = message;
    }

    public Long getPaymentId() { return paymentId; }

    public BigDecimal getAmount() { return amount; }

    public Integer getGiftPointsUsed() { return giftPointsUsed; }

    public BigDecimal getFinalAmount() { return finalAmount; }

    public String getStatus() { return status; }

    public String getMessage() { return message; }
}
