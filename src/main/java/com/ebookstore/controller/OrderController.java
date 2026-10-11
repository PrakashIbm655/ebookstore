package com.ebookstore.controller;

import com.ebookstore.dto.CartDto;
import com.ebookstore.dto.OrderDto;
import com.ebookstore.dto.OrderSummaryDto;
import com.ebookstore.security.AuthenticatedUser;
import com.ebookstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Order management and lifecycle endpoints")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /** GET /orders?page=0&size=10 — paginated order history, newest first. */
    @GetMapping
    @Operation(summary = "Get paginated order history for the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order history retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<OrderSummaryDto>> getOrderHistory(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrderHistory(principal.userId(), pageable));
    }

    /** GET /orders/{id} — full order detail including line items. */
    @GetMapping("/{id}")
    @Operation(summary = "Get order details by order ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order details retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderDto> getOrderById(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(principal.userId(), id));
    }

    /** POST /orders/{id}/reorder — re-adds all items from a past order to the active cart. */
    @PostMapping("/{id}/reorder")
    @Operation(summary = "Re-add all items from a past order to the active cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Items re-added to cart successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<CartDto> reorder(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long id) {
        return ResponseEntity.ok(orderService.reorder(principal.userId(), id));
    }

    /** DELETE /orders/{id} — cancels an order if within the 48-hour window. */
    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel an order if within the 48-hour cancellation window")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order cancelled successfully"),
            @ApiResponse(responseCode = "400", description = "Cancellation window expired or order cannot be cancelled"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<Void> cancelOrder(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long id) {
        orderService.cancelOrder(principal.userId(), id);
        return ResponseEntity.noContent().build();
    }
}
