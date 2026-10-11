package com.ebookstore.dto;

import java.math.BigDecimal;

public class OrderItemDto {

    private Long id;
    private Long productId;
    private String productTitle;
    private String productImageUrl;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    public OrderItemDto() {}

    public OrderItemDto(Long id, Long productId, String productTitle, String productImageUrl,
                        Integer quantity, BigDecimal unitPrice) {
        this.id = id;
        this.productId = productId;
        this.productTitle = productTitle;
        this.productImageUrl = productImageUrl;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public Long getId() { return id; }

    public Long getProductId() { return productId; }

    public String getProductTitle() { return productTitle; }

    public String getProductImageUrl() { return productImageUrl; }

    public Integer getQuantity() { return quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }

    public BigDecimal getSubtotal() { return subtotal; }
}
