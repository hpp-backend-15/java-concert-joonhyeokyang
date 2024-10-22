package com.joonhyeok.app.user;

import com.joonhyeok.app.user.domain.Account;
import com.joonhyeok.app.user.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class UserTest {
    @Test
    void 유저_포인트를_충전할수있다() throws Exception {
        //given
        User user = new User(null, new Account(0L, LocalDateTime.now()), 0);

        //when
        Account account = user.chargePoint(1000L);

        //then
        Assertions.assertThat(account.getBalance()).isEqualTo(1000L);

    }

    @Test
    void 유저_포인트충전은_음수일수없다() throws Exception {
        //given
        User user = new User(null, new Account(0L, LocalDateTime.now()), 0);

        //when
        //then
        Assertions.assertThatThrownBy(() -> user.chargePoint(-1000L))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    void 유저포인트사용을_할수있다() throws Exception {
        //given
        User user = new User(null, new Account(10000L, LocalDateTime.now()), 0);

        //when
        Account account = user.usePoint(1000L);

        //then
        Assertions.assertThat(account.getBalance()).isEqualTo(9000L);

    }

    @Test
    void 유저포인트사용이_음수일수없다() throws Exception {
        //given
        User user = new User(null, new Account(10000L, LocalDateTime.now()), 0);

        //when
        //then
        Assertions.assertThatThrownBy(() -> user.usePoint(-1000L))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    void 유저포인트사용이_잔고보다클수없다() throws Exception {
        //given
        User user = new User(null, new Account(0L, LocalDateTime.now()), 0);

        //when
        //then
        Assertions.assertThatThrownBy(() -> user.usePoint(1000L))
                .isInstanceOf(IllegalStateException.class);

    }
}
