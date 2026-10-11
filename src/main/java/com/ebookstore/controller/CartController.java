package com.ebookstore.controller;

import com.ebookstore.dto.AddToCartRequest;
import com.ebookstore.dto.CartDto;
import com.ebookstore.dto.UpdateCartItemRequest;
import com.ebookstore.security.AuthenticatedUser;
import com.ebookstore.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
@Tag(name = "Shopping Cart", description = "Shopping cart management endpoints")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /** GET /cart — returns the authenticated user's cart with all line items and total. */
    @GetMapping
    @Operation(summary = "Get current user's shopping cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<CartDto> getCart(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        return ResponseEntity.ok(cartService.getCart(principal.userId()));
    }

    /** POST /cart/items — adds a product to the cart (or increments quantity if already present). */
    @PostMapping("/items")
    @Operation(summary = "Add an item to the shopping cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item added to cart"),
            @ApiResponse(responseCode = "400", description = "Invalid request or insufficient stock"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<CartDto> addItem(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addItem(principal.userId(), request));
    }

    /** PUT /cart/items/{itemId} — updates the quantity of an existing cart item. */
    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update quantity of a cart item")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart item updated"),
            @ApiResponse(responseCode = "400", description = "Invalid quantity or insufficient stock"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Cart item not found")
    })
    public ResponseEntity<CartDto> updateItem(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateItem(principal.userId(), itemId, request));
    }

    /** DELETE /cart/items/{itemId} — removes a single item from the cart. */
    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove an item from the shopping cart")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cart item removed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Cart item not found")
    })
    public ResponseEntity<CartDto> removeItem(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItem(principal.userId(), itemId));
    }

    /** DELETE /cart — clears all items from the cart; returns 204 No Content. */
    @DeleteMapping
    @Operation(summary = "Clear all items from the shopping cart")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cart cleared successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Void> clearCart(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        cartService.clearCart(principal.userId());
        return ResponseEntity.noContent().build();
    }
}
