package com.ebookstore.service;

import java.math.BigDecimal;

/** Abstraction over a payment gateway. Replace {@link MockPaymentGateway} with a real implementation. */
public interface PaymentGateway {

    PaymentGatewayResult charge(BigDecimal amount, String method, String ref);

    /** Result returned by the gateway after attempting a charge. */
    record PaymentGatewayResult(boolean success, String transactionRef, String message) {}
}
