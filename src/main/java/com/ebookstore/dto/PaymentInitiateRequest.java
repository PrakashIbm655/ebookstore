package com.ebookstore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentInitiateRequest {

    @NotNull
    private Long cartId;

    @NotNull
    private Long addressId;

    @NotBlank
    private String method;

    @Min(0)
    private Integer giftPointsToRedeem = 0;

    public PaymentInitiateRequest() {}

    public Long getCartId() { return cartId; }

    public void setCartId(Long cartId) { this.cartId = cartId; }

    public Long getAddressId() { return addressId; }

    public void setAddressId(Long addressId) { this.addressId = addressId; }

    public String getMethod() { return method; }

    public void setMethod(String method) { this.method = method; }

    public Integer getGiftPointsToRedeem() {
        return giftPointsToRedeem != null ? giftPointsToRedeem : 0;
    }

    public void setGiftPointsToRedeem(Integer giftPointsToRedeem) {
        this.giftPointsToRedeem = giftPointsToRedeem;
    }
}
