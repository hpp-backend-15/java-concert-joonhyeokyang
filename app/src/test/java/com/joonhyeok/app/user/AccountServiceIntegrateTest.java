package com.joonhyeok.app.user;


import com.joonhyeok.app.user.application.AccountService;
import com.joonhyeok.app.user.application.dto.AccountBalanceQuery;
import com.joonhyeok.app.user.application.dto.AccountBalanceQueryResult;
import com.joonhyeok.app.user.application.dto.AccountChargeCommand;
import com.joonhyeok.app.user.domain.Account;
import com.joonhyeok.app.user.domain.User;
import com.joonhyeok.app.user.domain.UserRepository;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.BEFORE_EACH_TEST_METHOD;


@SpringBootTest
@ActiveProfiles("test")
@Sql("/ddl-test.sql")
@AutoConfigureEmbeddedDatabase(refresh = BEFORE_EACH_TEST_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AccountServiceIntegrateTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 유저가_돈을_충전하는경우_성공한다() {
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
    void 유저가_음수의돈을충전하는경우_실패한다() {
        //given
        User user = new User(null, new Account(0L, LocalDateTime.now()), 0);
        userRepository.save(user);

        //when
        AccountChargeCommand command = new AccountChargeCommand(1L, -1000L);

        //then
        Assertions.assertThatThrownBy(() -> accountService.charge(command))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    void 충전하려는_유저가_없는경우_실패한다() {
        //given
        User user = new User(null, new Account(0L, LocalDateTime.now()), 0);
        userRepository.save(user);

        //when
        AccountChargeCommand command = new AccountChargeCommand(-1L, 1000L);

        //then
        Assertions.assertThatThrownBy(() -> accountService.charge(command))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void 유저의_잔고를_조회할수_있다() {
        //given
        User user = new User(null, new Account(0L, LocalDateTime.now()), 0);
        userRepository.save(user);

        //when
        AccountBalanceQuery query = new AccountBalanceQuery(1L);

        //then
        AccountBalanceQueryResult result = accountService.query(query);
        Assertions.assertThat(result.balance()).isZero();
    }

    @Test
    void 조회하려는_유저가_없는경우_잔고를_조회할수없다() {
        //given
        User user = new User(null, new Account(0L, LocalDateTime.now()), 0);
        userRepository.save(user);

        //when
        AccountBalanceQuery query = new AccountBalanceQuery(-1L);

        //then
        Assertions.assertThatThrownBy(() -> accountService.query(query))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
