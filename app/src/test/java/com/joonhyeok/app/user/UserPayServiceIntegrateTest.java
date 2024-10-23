package com.joonhyeok.app.user;

import com.joonhyeok.app.concert.domain.Seat;
import com.joonhyeok.app.concert.domain.SeatRepository;
import com.joonhyeok.app.concert.domain.SeatStatus;
import com.joonhyeok.app.reservation.domain.Reservation;
import com.joonhyeok.app.reservation.domain.ReservationRepository;
import com.joonhyeok.app.reservation.domain.ReservationStatus;
import com.joonhyeok.app.user.application.UserPayService;
import com.joonhyeok.app.user.application.dto.UserPayCommand;
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

@SpringBootTest
@ActiveProfiles("test")
@Sql("/ddl-test.sql")
@AutoConfigureEmbeddedDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserPayServiceIntegrateTest {
    @Autowired
    private UserPayService userPayService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    void 예약한유저가없는경우_결제_실패() {
        //given
        seatRepository.save(new Seat(null, SeatStatus.PENDING, null, 0L, 0));
        reservationRepository.save(new Reservation(ReservationStatus.RESERVED, 1L, 1L));

        //when
        UserPayCommand command = new UserPayCommand(1L, 1L);

        //then
        Assertions.assertThatThrownBy(() -> userPayService.pay(command))
                .isInstanceOf(EntityNotFoundException.class);

    }

    @Test
    void 예약한좌석없는경우_결제_실패() {
        //given
        userRepository.save(new User(1L, new Account(0L, null), 0));
        reservationRepository.save(new Reservation(ReservationStatus.RESERVED, 1L, 1L));

        //when
        UserPayCommand command = new UserPayCommand(1L, 1L);

        //then
        Assertions.assertThatThrownBy(() -> userPayService.pay(command))
                .isInstanceOf(EntityNotFoundException.class);

    }

    @Test
    void 에약내역이_없는경우_결제_실패() {
        //given
        seatRepository.save(new Seat(null, SeatStatus.PENDING, null, 0L, 0));
        userRepository.save(new User(1L, new Account(0L, null), 0));

        //when
        UserPayCommand command = new UserPayCommand(1L, 1L);

        //then
        Assertions.assertThatThrownBy(() -> userPayService.pay(command))
                .isInstanceOf(EntityNotFoundException.class);

    }

    @Test
    void 에약내역과_유저가_일치하지않는경우_결제_실패() {
        //given
        seatRepository.save(new Seat(null, SeatStatus.PENDING, null, 0L, 0));
        userRepository.save(new User(null, new Account(0L, null), 0));
        userRepository.save(new User(null, new Account(0L, null), 0));
        reservationRepository.save(new Reservation(ReservationStatus.RESERVED, 1L, 1L));

        //when
        UserPayCommand command = new UserPayCommand(2L, 1L);

        //then
        Assertions.assertThatThrownBy(() -> userPayService.pay(command))
                .isInstanceOf(IllegalArgumentException.class);

    }

}
