package com.joonhyeok.app.user.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Slf4j
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
        log.info("try deposit with amount of {}, current balance = {}", amount, this.balance);
        if (amount <= 0) {
            log.error("충전 금액은 음수일 수 없습니다. amount = {}", amount);
            throw new IllegalArgumentException("충전 금액은 음수일 수 없습니다. amount = " + amount);
        }
        log.info("complete deposit with amount of {}, current balance = {}", amount, this.balance);
        this.balance += amount;
        this.modifiedAt = LocalDateTime.now();
    }

    public void withdraw(long amount) {
        log.info("try withdraw with amount of {}, current balance = {}", amount, this.balance);
        if (amount < 0) {
            log.error("출금 금액은 음수일 수 없습니다. amount = {}", amount);
            throw new IllegalArgumentException("출금 금액은 음수일 수 없습니다. amount = " + amount);
        }
        if (this.balance < amount) {
            log.error("잔고 이상 출금할 수 없습니다. balance = {}, withdraw amount = {}", this.balance, amount);
            throw new IllegalStateException("잔고 이상 출금할 수 없습니다. balance = " + this.balance + ", withdraw amount = " + amount);
        }
        log.info("complete withdraw with amount of {}, current balance = {}", amount, this.balance);
        this.balance -= amount;
        this.modifiedAt = LocalDateTime.now();
    }
}
