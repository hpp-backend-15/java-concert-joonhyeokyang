package com.joonhyeok.app.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
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
    @AttributeOverrides({
            @AttributeOverride(name = "balance", column = @Column(name = "users_account_balance")),
            @AttributeOverride(name = "balance", column = @Column(name = "users_account_modifiedAt"))
    })
    private Account account;

    @Version
    private int version;

    public void chargePoint(long amount) {
        this.account.deposit(amount);
    }

    public void usePoint(long amount) {
        this.account.withdraw(amount);
    }
}
