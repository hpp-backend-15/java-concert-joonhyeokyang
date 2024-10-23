package com.joonhyeok.app.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Embeddable
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class Account {

    @Column(name = "accounts_balance")
    private Long balance;

    @Column(name = "accounts_modifiedAt")
    private LocalDateTime modifiedAt;

    public void deposit(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 음수일 수 없습니다. amount = " + amount);
        }
        this.balance += amount;
        this.modifiedAt = LocalDateTime.now();
    }

    public void withdraw(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("출금 금액은 음수일 수 없습니다. amount = " + amount);
        }
        if (this.balance < amount) {
            throw new IllegalStateException("잔고 이상 출금할 수 없습니다. balance = " + this.balance + ", withdraw amount = " + amount);
        }
        this.balance -= amount;
        this.modifiedAt = LocalDateTime.now();
    }
}
