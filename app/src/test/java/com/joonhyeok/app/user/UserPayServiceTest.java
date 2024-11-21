package com.joonhyeok.app.user;

import com.joonhyeok.app.concert.SeatMemoryRepository;
import com.joonhyeok.app.concert.domain.Seat;
import com.joonhyeok.app.concert.domain.SeatRepository;
import com.joonhyeok.app.concert.domain.SeatStatus;
import com.joonhyeok.app.queue.QueueMemoryRepository;
import com.joonhyeok.app.queue.domain.QueueRepository;
import com.joonhyeok.app.reservation.ReservationMemoryRepository;
import com.joonhyeok.app.reservation.domain.Reservation;
import com.joonhyeok.app.reservation.domain.ReservationRepository;
import com.joonhyeok.app.reservation.domain.ReservationStatus;
import com.joonhyeok.app.user.application.UserPayService;
import com.joonhyeok.app.user.application.dto.UserPayCommand;
import com.joonhyeok.app.user.domain.user.Account;
import com.joonhyeok.app.user.domain.user.User;
import com.joonhyeok.app.user.domain.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserPayServiceTest {
    private UserPayService userPayService;
    private UserRepository userRepository;
    private SeatRepository seatRepository;
    private ReservationRepository reservationRepository;

    @BeforeEach
    public void setUp() {
        userRepository = new UserMemoryRepository();
        reservationRepository = new ReservationMemoryRepository();
        seatRepository = new SeatMemoryRepository();
        QueueRepository queueRepository = new QueueMemoryRepository();
        userPayService = new UserPayService(userRepository, seatRepository, reservationRepository, queueRepository, new DummyEventPublisher());
    }

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
