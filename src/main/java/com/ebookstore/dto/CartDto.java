package com.ebookstore.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class CartDto {

    private Long id;
    private Long userId;
    private List<CartItemDto> items;
    private BigDecimal totalAmount;
    private Instant updatedAt;

    public CartDto(Long id, Long userId, List<CartItemDto> items, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.items = items;
        this.totalAmount = items.stream()
                .map(CartItemDto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public List<CartItemDto> getItems() {
        return items;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
