package com.ebookstore.dto;

import java.math.BigDecimal;

public class CartItemDto {

    private Long id;
    private Long productId;
    private String productTitle;
    private String productImageUrl;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal subtotal;

    public CartItemDto(Long id, Long productId, String productTitle, String productImageUrl,
                       BigDecimal unitPrice, int quantity) {
        this.id = id;
        this.productId = productId;
        this.productTitle = productTitle;
        this.productImageUrl = productImageUrl;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}
