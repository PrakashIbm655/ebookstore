package com.ebookstore.dto;

import com.ebookstore.model.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class OrderDto {

    private Long id;
    private Long userId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private Long deliveryAddressId;
    private String deliveryCity;
    private String deliveryPincode;
    private List<OrderItemDto> items;
    private Instant placedAt;
    private Instant updatedAt;

    public OrderDto() {}

    public OrderDto(Long id, Long userId, OrderStatus status, BigDecimal totalAmount,
                    Long deliveryAddressId, String deliveryCity, String deliveryPincode,
                    List<OrderItemDto> items, Instant placedAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.totalAmount = totalAmount;
        this.deliveryAddressId = deliveryAddressId;
        this.deliveryCity = deliveryCity;
        this.deliveryPincode = deliveryPincode;
        this.items = items;
        this.placedAt = placedAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }

    public Long getUserId() { return userId; }

    public OrderStatus getStatus() { return status; }

    public BigDecimal getTotalAmount() { return totalAmount; }

    public Long getDeliveryAddressId() { return deliveryAddressId; }

    public String getDeliveryCity() { return deliveryCity; }

    public String getDeliveryPincode() { return deliveryPincode; }

    public List<OrderItemDto> getItems() { return items; }

    public Instant getPlacedAt() { return placedAt; }

    public Instant getUpdatedAt() { return updatedAt; }
}
