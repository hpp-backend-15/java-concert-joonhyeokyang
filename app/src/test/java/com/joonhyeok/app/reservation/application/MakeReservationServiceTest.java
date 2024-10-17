package com.joonhyeok.app.reservation.application;

import com.joonhyeok.app.concert.domain.*;
import com.joonhyeok.app.concert.infra.ConcertMemoryRepository;
import com.joonhyeok.app.concert.infra.SeatMemoryRepository;
import com.joonhyeok.app.reservation.application.dto.MakeReservationCommand;
import com.joonhyeok.app.reservation.application.dto.MakeReservationResult;
import com.joonhyeok.app.reservation.domain.Reservation;
import com.joonhyeok.app.reservation.domain.ReservationRepository;
import com.joonhyeok.app.reservation.infra.ReservationMemoryRepository;
import com.joonhyeok.app.user.User;
import com.joonhyeok.app.user.UserMemoryRepository;
import com.joonhyeok.app.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static com.joonhyeok.app.concert.domain.ConcertTestHelper.*;
import static com.joonhyeok.app.reservation.domain.ReservationStatus.RESERVED;

public class MakeReservationServiceTest {
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
        makeReservationService = new MakeReservationService(reservationRepository, concertRepository, seatRepository, userRepository);

    }

    @Test
    void 선택좌석이_예약가능상태라면_예약할수있다_좌석상태_변경확인() throws Exception {
        //given
        Concert concert = createConcert(createAllAvailableSeats());
        User user = new User(0L);
        userRepository.save(user);
        concertRepository.save(concert);

        //when
        MakeReservationResult result = makeReservationService.reserve(new MakeReservationCommand(0L, 0L));

        //then
        Reservation reservation = reservationRepository.findById(result.reservationId()).get();
        Assertions.assertThat(reservation).isNotNull();
        Assertions.assertThat(reservation.getStatus()).isEqualTo(RESERVED);

        Seat seat = seatRepository.findById(reservation.getSeatId()).get();
        Assertions.assertThat(seat).isNotNull();
        Assertions.assertThat(seat.getStatus()).isEqualTo(SeatStatus.PENDING);
    }
    // 유저가 없는 경우

    @Test
    void 유저가없다면_예약할수없다() throws Exception {
        //given
        Concert concert = createConcert(createAllAvailableSeats());
        concertRepository.save(concert);

        //when
        //then
        Assertions.assertThatThrownBy(() -> makeReservationService.reserve(new MakeReservationCommand(0L, 0L)))
                .isInstanceOf(EntityNotFoundException.class);

    }

    @Test
    void 좌석이없다면_예약할수없다() throws Exception {
        //given
        Concert concert = createConcert(new ArrayList<>());
        User user = new User(0L);
        userRepository.save(user);
        concertRepository.save(concert);

        //when
        //then
        Assertions.assertThatThrownBy(() -> makeReservationService.reserve(new MakeReservationCommand(0L, 0L)))
                .isInstanceOf(EntityNotFoundException.class);

    }

    @Test
    void 선택좌석이_예약불가능하다면_예약할수없다() throws Exception {
        //given
        Concert concert = createConcert(createAllUnavailableSeats());
        User user = new User(0L);
        userRepository.save(user);
        concertRepository.save(concert);

        //when
        //then
        Assertions.assertThatThrownBy(() -> makeReservationService.reserve(new MakeReservationCommand(0L, 0L)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 선택된 좌석입니다");
    }
}
