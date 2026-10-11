package com.ebookstore.controller;

import com.ebookstore.dto.GiftPointsDto;
import com.ebookstore.dto.PaymentConfirmResponse;
import com.ebookstore.dto.PaymentInitiateRequest;
import com.ebookstore.dto.PaymentInitiateResponse;
import com.ebookstore.security.AuthenticatedUser;
import com.ebookstore.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Payments", description = "Mock payment processing and gift points endpoints")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /** GET /users/me/gift-points — returns current gift point balance. */
    @GetMapping("/users/me/gift-points")
    @Operation(summary = "Get current gift point balance")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Gift points retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<GiftPointsDto> getGiftPoints(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        return ResponseEntity.ok(paymentService.getGiftPoints(principal.userId()));
    }

    /** POST /payments/initiate — creates a PENDING payment from cart, address, and method. */
    @PostMapping("/payments/initiate")
    @Operation(summary = "Initiate a payment for the shopping cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment initiated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or empty cart"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Cart or address not found")
    })
    public ResponseEntity<PaymentInitiateResponse> initiatePayment(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody PaymentInitiateRequest request) {
        return ResponseEntity.ok(paymentService.initiatePayment(principal.userId(), request));
    }

    /** POST /payments/confirm/{paymentId} — confirms a PENDING payment via the mock gateway. */
    @PostMapping("/payments/confirm/{paymentId}")
    @Operation(summary = "Confirm a pending payment via mock payment gateway")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment confirmation processed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<PaymentConfirmResponse> confirmPayment(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.confirmPayment(principal.userId(), paymentId));
    }
}
