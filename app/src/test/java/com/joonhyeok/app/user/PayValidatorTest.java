package com.joonhyeok.app.user;

import com.joonhyeok.app.concert.domain.Seat;
import com.joonhyeok.app.concert.domain.SeatStatus;
import com.joonhyeok.app.reservation.domain.Reservation;
import com.joonhyeok.app.reservation.domain.ReservationStatus;
import com.joonhyeok.app.user.domain.user.Account;
import com.joonhyeok.app.user.domain.user.PayValidator;
import com.joonhyeok.app.user.domain.user.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PayValidatorTest {
    private PayValidator payValidator;

    @BeforeEach
    public void setUp() {
        payValidator = new PayValidator();
    }

    @Test
    void 좌석이_결제대기중이_아니라면_예외발생_예약대기케이스() {
        //given
        Seat seat = new Seat(1L, SeatStatus.AVAILABLE, null, 0L, 0);
        User user = new User(1L, new Account(0L, null), 0);
        Reservation reservation = new Reservation(ReservationStatus.RESERVED, 1L, 1L);

        //when
        //then
        Assertions.assertThatThrownBy(() -> payValidator.validate(user, seat, reservation))
                .isInstanceOf(IllegalStateException.class);

    }

    @Test
    void 좌석이_결제대기중이_아니라면_예외발생_만료케이스() {
        //given
        Seat seat = new Seat(1L, SeatStatus.UNAVAILABLE, null, 0L, 0);
        User user = new User(1L, new Account(0L, null), 0);
        Reservation reservation = new Reservation(ReservationStatus.RESERVED, 1L, 1L);

        //when
        //then
        Assertions.assertThatThrownBy(() -> payValidator.validate(user, seat, reservation))
                .isInstanceOf(IllegalStateException.class);

    }

    @Test
    void 예약이_결제대기상태아니라면_예외발생_이미결제된경우() {
        //given
        Seat seat = new Seat(1L, SeatStatus.PENDING, null, 0L, 0);
        User user = new User(1L, new Account(0L, null), 0);
        Reservation reservation = new Reservation(ReservationStatus.PAYED, 1L, 1L);

        //when
        //then
        Assertions.assertThatThrownBy(() -> payValidator.validate(user, seat, reservation))
                .isInstanceOf(IllegalStateException.class);

    }

    @Test
    void 예약이_결제대기상태아니라면_예외발생_이미취소된경우() {
        //given
        Seat seat = new Seat(1L, SeatStatus.PENDING, null, 0L, 0);
        User user = new User(1L, new Account(0L, null), 0);
        Reservation reservation = new Reservation(ReservationStatus.CANCELLED, 1L, 1L);

        //when
        //then
        Assertions.assertThatThrownBy(() -> payValidator.validate(user, seat, reservation))
                .isInstanceOf(IllegalStateException.class);

    }

    @Test
    void 결제시도시_유저_잔액이_부족한경우() {
        //given
        Seat seat = new Seat(1L, SeatStatus.PENDING, null, 1L, 0);
        User user = new User(1L, new Account(0L, null), 0);
        Reservation reservation = new Reservation(ReservationStatus.RESERVED, 1L, 1L);

        //when
        //then
        Assertions.assertThatThrownBy(() -> payValidator.validate(user, seat, reservation))
                .isInstanceOf(IllegalStateException.class);

    }

    @Test
    void 결제시도시_예약된유저와_결제하려는유저가_다른경우() {
        Seat seat = new Seat(1L, SeatStatus.PENDING, null, 0L, 0);
        User user = new User(2L, new Account(0L, null), 0);
        Reservation reservation = new Reservation(ReservationStatus.RESERVED, 1L, 1L);

        //when
        //then
        Assertions.assertThatThrownBy(() -> payValidator.validate(user, seat, reservation))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    void 결제시도시_예약된좌석과_결제하려는좌석이_다른경우() {
        Seat seat = new Seat(2L, SeatStatus.PENDING, null, 0L, 0);
        User user = new User(1L, new Account(0L, null), 0);
        Reservation reservation = new Reservation(ReservationStatus.RESERVED, 1L, 1L);

        //when
        //then
        Assertions.assertThatThrownBy(() -> payValidator.validate(user, seat, reservation))
                .isInstanceOf(IllegalArgumentException.class);

    }
}
