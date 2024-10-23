package com.joonhyeok.app.user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class User {
    @Id
    @GeneratedValue
    @Column(name = "users_id")
    private Long id;

    @Embedded
    @AttributeOverride(name = "balance", column = @Column(name = "users_account_balance"))
    @AttributeOverride(name = "modifiedAt", column = @Column(name = "users_account_modifiedAt"))
    private Account account;

    @Version
    private int version;

    public Account chargePoint(long amount) {
        this.account.deposit(amount);
        return this.account;
    }

    public Account usePoint(long amount) {
        this.account.withdraw(amount);
        return this.account;
    }
}
