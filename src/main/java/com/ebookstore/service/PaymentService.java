package com.ebookstore.service;

import com.ebookstore.dto.GiftPointsDto;
import com.ebookstore.dto.PaymentConfirmResponse;
import com.ebookstore.dto.PaymentInitiateRequest;
import com.ebookstore.dto.PaymentInitiateResponse;
import com.ebookstore.exception.ResourceNotFoundException;
import com.ebookstore.model.Cart;
import com.ebookstore.model.CartItem;
import com.ebookstore.model.GiftPointsLedger;
import com.ebookstore.model.Payment;
import com.ebookstore.model.PaymentStatus;
import com.ebookstore.model.User;
import com.ebookstore.repository.AddressRepository;
import com.ebookstore.repository.CartItemRepository;
import com.ebookstore.repository.CartRepository;
import com.ebookstore.repository.GiftPointsLedgerRepository;
import com.ebookstore.repository.PaymentRepository;
import com.ebookstore.repository.UserRepository;
import com.ebookstore.service.PaymentGateway.PaymentGatewayResult;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class PaymentService {

    private static final Set<String> VALID_METHODS = Set.of("CREDIT_CARD", "DEBIT_CARD");
    private static final int WELCOME_POINTS = 100;

    private final PaymentRepository paymentRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;
    private final GiftPointsLedgerRepository ledgerRepository;
    private final UserRepository userRepository;
    private final PaymentGateway paymentGateway;
    private final OrderService orderService;

    public PaymentService(PaymentRepository paymentRepository,
                          CartRepository cartRepository,
                          CartItemRepository cartItemRepository,
                          AddressRepository addressRepository,
                          GiftPointsLedgerRepository ledgerRepository,
                          UserRepository userRepository,
                          PaymentGateway paymentGateway,
                          @Lazy OrderService orderService) {
        this.paymentRepository = paymentRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.addressRepository = addressRepository;
        this.ledgerRepository = ledgerRepository;
        this.userRepository = userRepository;
        this.paymentGateway = paymentGateway;
        this.orderService = orderService;
    }

    /**
     * Returns the gift point balance for the user. Creates a ledger entry with
     * {@value WELCOME_POINTS} welcome points if one does not yet exist.
     */
    public GiftPointsDto getGiftPoints(Long userId) {
        GiftPointsLedger ledger = getOrCreateLedger(userId);
        return new GiftPointsDto(userId, ledger.getBalance());
    }

    /**
     * Validates the cart, address, payment method and gift points, then creates
     * a {@link Payment} record in {@link PaymentStatus#PENDING} state.
     */
    public PaymentInitiateResponse initiatePayment(Long userId, PaymentInitiateRequest request) {
        // Validate method
        String method = request.getMethod().toUpperCase();
        if (!VALID_METHODS.contains(method)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid payment method. Allowed: CREDIT_CARD, DEBIT_CARD");
        }

        // Load and validate cart
        Cart cart = cartRepository.findById(request.getCartId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found: " + request.getCartId()));
        if (!cart.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cart does not belong to current user");
        }
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        if (items.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        // Load and validate address
        com.ebookstore.model.Address address = addressRepository.findByIdAndUserId(request.getAddressId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found: " + request.getAddressId()));

        // Compute cart total
        BigDecimal cartTotal = items.stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Validate gift points
        int pointsToRedeem = request.getGiftPointsToRedeem();
        if (pointsToRedeem > 0) {
            GiftPointsLedger ledger = getOrCreateLedger(userId);
            int maxRedeemable = Math.min(ledger.getBalance(), cartTotal.intValue());
            if (pointsToRedeem > maxRedeemable) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Gift points to redeem (" + pointsToRedeem
                        + ") exceed the allowed maximum (" + maxRedeemable + ")");
            }
        }

        BigDecimal finalAmount = cartTotal.subtract(BigDecimal.valueOf(pointsToRedeem));

        // Create payment record
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        Payment payment = new Payment(user, cart, finalAmount, method, pointsToRedeem);
        payment.setAddress(address);
        payment = paymentRepository.save(payment);

        return new PaymentInitiateResponse(
                payment.getId(),
                cartTotal,
                pointsToRedeem,
                finalAmount,
                "PENDING",
                "Payment initiated. Call /payments/confirm/" + payment.getId() + " to confirm.");
    }

    /**
     * Calls the payment gateway to charge the amount. On success transitions to
     * {@link PaymentStatus#CONFIRMED} and deducts gift points; on failure transitions
     * to {@link PaymentStatus#FAILED}.
     */
    public PaymentConfirmResponse confirmPayment(Long userId, Long paymentId) {
        Payment payment = paymentRepository.findByIdAndUserId(paymentId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Payment is not in PENDING state: " + payment.getStatus());
        }

        PaymentGatewayResult result = paymentGateway.charge(
                payment.getAmount(), payment.getMethod(), String.valueOf(payment.getId()));

        if (result.success()) {
            payment.setStatus(PaymentStatus.CONFIRMED);
            payment.setTransactionRef(result.transactionRef());

            // Deduct gift points from ledger
            if (payment.getGiftPointsUsed() > 0) {
                GiftPointsLedger ledger = getOrCreateLedger(userId);
                ledger.setBalance(Math.max(0, ledger.getBalance() - payment.getGiftPointsUsed()));
                ledgerRepository.save(ledger);
            }

            paymentRepository.save(payment);

            // Create order from confirmed payment
            orderService.createFromPayment(payment.getId());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setTransactionRef(result.transactionRef());
            paymentRepository.save(payment);
        }

        return new PaymentConfirmResponse(
                payment.getId(),
                payment.getStatus().name(),
                payment.getTransactionRef(),
                result.message());
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private GiftPointsLedger getOrCreateLedger(Long userId) {
        return ledgerRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
            return ledgerRepository.save(new GiftPointsLedger(user, WELCOME_POINTS));
        });
    }
}
