package com.ebookstore.dto;

import com.ebookstore.model.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;

public class OrderSummaryDto {

    private Long id;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private int itemCount;
    private Instant placedAt;

    public OrderSummaryDto() {}

    public OrderSummaryDto(Long id, OrderStatus status, BigDecimal totalAmount,
                           int itemCount, Instant placedAt) {
        this.id = id;
        this.status = status;
        this.totalAmount = totalAmount;
        this.itemCount = itemCount;
        this.placedAt = placedAt;
    }

    public Long getId() { return id; }

    public OrderStatus getStatus() { return status; }

    public BigDecimal getTotalAmount() { return totalAmount; }

    public int getItemCount() { return itemCount; }

    public Instant getPlacedAt() { return placedAt; }
}
