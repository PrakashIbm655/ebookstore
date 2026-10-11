package com.ebookstore.service;

import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Mock implementation of {@link PaymentGateway}. Always returns success with a
 * generated transaction reference. Replace with a real gateway implementation
 * by providing an alternative {@link PaymentGateway} bean.
 */
@Service
public class MockPaymentGateway implements PaymentGateway {

    @Override
    public PaymentGatewayResult charge(BigDecimal amount, String method, String ref) {
        String transactionRef = "MOCK-" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
        return new PaymentGatewayResult(true, transactionRef, "Payment processed successfully");
    }
}
