package com.joonhyeok.app.user;


import com.joonhyeok.app.user.application.dto.AccountBalanceQuery;
import com.joonhyeok.app.user.application.dto.AccountBalanceQueryResult;
import com.joonhyeok.app.user.application.AccountService;
import com.joonhyeok.app.user.application.dto.AccountChargeCommand;
import com.joonhyeok.app.user.domain.Account;
import com.joonhyeok.app.user.domain.User;
import com.joonhyeok.app.user.domain.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class AccountServiceTest {
    private AccountService accountService;
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository = new UserMemoryRepository();
        accountService = new AccountService(userRepository);
    }

    @Test
    void 유저가_돈을_충전하는경우_성공한다() throws Exception {
        //given
        User user = new User(null, new Account(0L, LocalDateTime.now()), 0);
        userRepository.save(user);

        //when
        accountService.charge(new AccountChargeCommand(1L, 1000L));

        //then
        User chargedUser = userRepository.findById(1L).orElseThrow();
        Assertions.assertThat(chargedUser.getAccount().getBalance()).isEqualTo(1000);
    }

    @Test
    void 유저가_음수의돈을충전하는경우_실패한다() throws Exception {
        //given
        User user = new User(null, new Account(0L, LocalDateTime.now()), 0);
        userRepository.save(user);

        //when
        //then
        Assertions.assertThatThrownBy(() -> accountService.charge(new AccountChargeCommand(1L, -1000L)))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    void 충전하려는_유저가_없는경우_실패한다() throws Exception {
        //given
        User user = new User(null, new Account(0L, LocalDateTime.now()), 0);
        userRepository.save(user);

        //when
        //then
        Assertions.assertThatThrownBy(() -> accountService.charge(new AccountChargeCommand(-1L, 1000L)))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void 유저의_잔고를_조회할수_있다() throws Exception {
        //given
        User user = new User(null, new Account(0L, LocalDateTime.now()), 0);
        userRepository.save(user);

        //when
        AccountBalanceQueryResult result = accountService.query(new AccountBalanceQuery(1L));

        //then
        Assertions.assertThat(result.balance()).isEqualTo(0L);
    }

    @Test
    void 조회하려는_유저가_없는경우_잔고를_조회할수없다() throws Exception {
        //given
        User user = new User(null, new Account(0L, LocalDateTime.now()), 0);
        userRepository.save(user);

        //when
        //then
        Assertions.assertThatThrownBy(()-> accountService.query(new AccountBalanceQuery(-1L)))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
