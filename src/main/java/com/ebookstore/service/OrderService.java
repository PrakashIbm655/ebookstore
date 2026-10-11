package com.ebookstore.service;

import com.ebookstore.dto.AddToCartRequest;
import com.ebookstore.dto.CartDto;
import com.ebookstore.dto.OrderDto;
import com.ebookstore.dto.OrderItemDto;
import com.ebookstore.dto.OrderSummaryDto;
import com.ebookstore.exception.ResourceNotFoundException;
import com.ebookstore.model.CartItem;
import com.ebookstore.model.Order;
import com.ebookstore.model.OrderItem;
import com.ebookstore.model.OrderStatus;
import com.ebookstore.model.Payment;
import com.ebookstore.model.PaymentStatus;
import com.ebookstore.repository.CartItemRepository;
import com.ebookstore.repository.OrderItemRepository;
import com.ebookstore.repository.OrderRepository;
import com.ebookstore.repository.PaymentRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final CartItemRepository cartItemRepository;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        PaymentRepository paymentRepository,
                        CartItemRepository cartItemRepository,
                        @Lazy CartService cartService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartService = cartService;
    }

    /**
     * Creates an {@link Order} from a confirmed payment, snapshotting each cart item's price,
     * then clears the user's cart.
     *
     * @param paymentId ID of a {@link Payment} in {@link PaymentStatus#CONFIRMED} state
     * @return full {@link OrderDto} for the newly created order
     */
    public OrderDto createFromPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));

        if (payment.getStatus() != PaymentStatus.CONFIRMED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot create order — payment is not CONFIRMED: " + payment.getStatus());
        }

        if (payment.getAddress() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Payment has no delivery address attached");
        }

        // Snapshot cart items
        List<CartItem> cartItems = cartItemRepository.findByCartId(payment.getCart().getId());
        if (cartItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot create order — cart is empty");
        }

        // Create order
        Order order = new Order(payment.getUser(), payment, payment.getAmount(), payment.getAddress());
        order = orderRepository.save(order);

        // Create order items (snapshot unit price at time of order)
        final Order savedOrder = order;
        List<OrderItem> orderItems = cartItems.stream()
                .map(ci -> new OrderItem(savedOrder, ci.getProduct(),
                        ci.getQuantity(), ci.getProduct().getPrice()))
                .toList();
        orderItemRepository.saveAll(orderItems);

        // Clear the cart
        cartService.clearCart(payment.getUser().getId());

        return toOrderDto(order, orderItems);
    }

    /**
     * Returns paginated order history for a user, newest first.
     */
    @Transactional(readOnly = true)
    public Page<OrderSummaryDto> getOrderHistory(Long userId, Pageable pageable) {
        return orderRepository.findByUserIdOrderByPlacedAtDesc(userId, pageable)
                .map(order -> {
                    int itemCount = orderItemRepository.findByOrderId(order.getId()).size();
                    return new OrderSummaryDto(
                            order.getId(),
                            order.getStatus(),
                            order.getTotalAmount(),
                            itemCount,
                            order.getPlacedAt());
                });
    }

    /**
     * Returns the full order detail including line items.
     *
     * @throws ResponseStatusException 404 if order not found or does not belong to the user
     */
    @Transactional(readOnly = true)
    public OrderDto getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        return toOrderDto(order, items);
    }

    /**
     * Re-adds all items from a past order into the user's active cart.
     *
     * @return updated {@link CartDto}
     */
    public CartDto reorder(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        CartDto cart = null;
        for (OrderItem item : items) {
            AddToCartRequest req = new AddToCartRequest();
            req.setProductId(item.getProduct().getId());
            req.setQuantity(item.getQuantity());
            cart = cartService.addItem(userId, req);
        }
        // If somehow the order had no items, return the current cart state
        if (cart == null) {
            cart = cartService.getCart(userId);
        }
        return cart;
    }

    /**
     * Cancels an order if it is within 48 hours of placement and in a cancellable status
     * ({@code PLACED} or {@code PROCESSING}).
     *
     * @throws ResponseStatusException 422 if the 48-hour window has passed
     * @throws ResponseStatusException 400 if the order is not cancellable
     */
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order is already cancelled");
        }

        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot cancel order in status: " + order.getStatus());
        }

        if (Duration.between(order.getPlacedAt(), Instant.now()).toHours() >= 48) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Cannot cancel order older than 48 hours");
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private OrderDto toOrderDto(Order order, List<OrderItem> items) {
        List<OrderItemDto> itemDtos = items.stream()
                .map(oi -> new OrderItemDto(
                        oi.getId(),
                        oi.getProduct().getId(),
                        oi.getProduct().getTitle(),
                        oi.getProduct().getImageUrl(),
                        oi.getQuantity(),
                        oi.getUnitPrice()))
                .toList();

        return new OrderDto(
                order.getId(),
                order.getUser().getId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getDeliveryAddress().getId(),
                order.getDeliveryAddress().getCity(),
                order.getDeliveryAddress().getPincode(),
                itemDtos,
                order.getPlacedAt(),
                order.getUpdatedAt());
    }
}
