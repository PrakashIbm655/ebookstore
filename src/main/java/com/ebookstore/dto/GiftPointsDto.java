package com.ebookstore.dto;

public class GiftPointsDto {

    private Long userId;
    private Integer balance;

    public GiftPointsDto() {}

    public GiftPointsDto(Long userId, Integer balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public Long getUserId() { return userId; }

    public Integer getBalance() { return balance; }
}
