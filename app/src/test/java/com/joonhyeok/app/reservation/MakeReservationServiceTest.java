package com.joonhyeok.app.reservation;

import com.joonhyeok.app.concert.ConcertMemoryRepository;
import com.joonhyeok.app.concert.SeatMemoryRepository;
import com.joonhyeok.app.concert.domain.*;
import com.joonhyeok.app.reservation.application.MakeReservationService;
import com.joonhyeok.app.reservation.application.dto.MakeReservationCommand;
import com.joonhyeok.app.reservation.application.dto.MakeReservationResult;
import com.joonhyeok.app.reservation.domain.Reservation;
import com.joonhyeok.app.reservation.domain.ReservationRepository;
import com.joonhyeok.app.user.UserMemoryRepository;
import com.joonhyeok.app.user.domain.Account;
import com.joonhyeok.app.user.domain.User;
import com.joonhyeok.app.user.domain.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.joonhyeok.app.concert.ConcertTestHelper.createConcertWithAvailableSeats;
import static com.joonhyeok.app.concert.ConcertTestHelper.createConcertWithUnavailableSeats;
import static com.joonhyeok.app.reservation.domain.ReservationStatus.RESERVED;

class MakeReservationServiceTest {
    ConcertRepository concertRepository;
    ReservationRepository reservationRepository;
    SeatRepository seatRepository;
    UserRepository userRepository;

    MakeReservationService makeReservationService;

    @BeforeEach
    void setUp() {
        reservationRepository = new ReservationMemoryRepository();
        seatRepository = new SeatMemoryRepository();
        concertRepository = new ConcertMemoryRepository(seatRepository);
        userRepository = new UserMemoryRepository();
        makeReservationService = new MakeReservationService(reservationRepository, seatRepository, userRepository);

    }

    @Test
    void 선택좌석이_예약가능상태라면_예약할수있다_좌석상태_변경확인() {
        //given
        Concert concert = createConcertWithAvailableSeats();
        User user = new User(null, new Account(0L, LocalDateTime.now()), 0);

        userRepository.save(user);
        concertRepository.save(concert);

        //when
        MakeReservationResult result = makeReservationService.reserve(new MakeReservationCommand(1L, 1L));

        //then
        Reservation reservation = reservationRepository.findById(result.reservationId()).get();
        Assertions.assertThat(reservation).isNotNull();
        Assertions.assertThat(reservation.getStatus()).isEqualTo(RESERVED);

        Seat seat = seatRepository.findById(reservation.getSeatId()).get();
        Assertions.assertThat(seat).isNotNull();
        Assertions.assertThat(seat.getStatus()).isEqualTo(SeatStatus.PENDING);
    }

    @Test
    void 유저가없다면_예약할수없다() {
        //given
        Concert concert = createConcertWithAvailableSeats();
        concertRepository.save(concert);

        //when
        MakeReservationCommand command = new MakeReservationCommand(1L, 1L);

        //then
        Assertions.assertThatThrownBy(() -> makeReservationService.reserve(command))
                .isInstanceOf(EntityNotFoundException.class);

    }

    @Test
    void 선택좌석이_예약불가능하다면_예약할수없다() {
        //given
        Concert concert = createConcertWithUnavailableSeats();
        User user = new User(null, new Account(0L, LocalDateTime.now()), 0);

        userRepository.save(user);
        concertRepository.save(concert);

        //when
        MakeReservationCommand command = new MakeReservationCommand(1L, 1L);

        //then
        Assertions.assertThatThrownBy(() -> makeReservationService.reserve(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 선택된 좌석입니다");
    }
}
