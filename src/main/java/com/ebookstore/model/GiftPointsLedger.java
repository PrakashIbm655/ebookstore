package com.ebookstore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "gift_points_ledger")
public class GiftPointsLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private Integer balance = 0;

    protected GiftPointsLedger() {
        // required by JPA
    }

    public GiftPointsLedger(User user, Integer initialBalance) {
        this.user = user;
        this.balance = initialBalance != null ? initialBalance : 0;
    }

    public Long getId() { return id; }

    public User getUser() { return user; }

    public Integer getBalance() { return balance; }

    public void setBalance(Integer balance) { this.balance = balance; }
}
