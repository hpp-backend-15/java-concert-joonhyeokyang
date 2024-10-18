package com.joonhyeok.app.reservation;

import com.joonhyeok.app.concert.domain.*;
import com.joonhyeok.app.reservation.application.MakeReservationService;
import com.joonhyeok.app.reservation.application.dto.MakeReservationCommand;
import com.joonhyeok.app.reservation.application.dto.MakeReservationResult;
import com.joonhyeok.app.reservation.domain.Reservation;
import com.joonhyeok.app.reservation.domain.ReservationRepository;
import com.joonhyeok.app.user.User;
import com.joonhyeok.app.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static com.joonhyeok.app.concert.ConcertTestHelper.createConcertWithAvailableSeats;
import static com.joonhyeok.app.concert.ConcertTestHelper.createConcertWithUnavailableSeats;
import static com.joonhyeok.app.reservation.domain.ReservationStatus.RESERVED;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yaml")
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class MakeReservationServiceIntegrateTest {

    @Autowired
    ConcertRepository concertRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    SeatRepository seatRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MakeReservationService makeReservationService;

    @Test
    void 선택좌석이_예약가능상태라면_예약할수있다_좌석상태_변경확인() throws Exception {
        //given
        Concert concert = createConcertWithAvailableSeats();
        User user = new User(null);
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
    // 유저가 없는 경우

    @Test
    void 유저가없다면_예약할수없다() throws Exception {
        //given
        Concert concert = createConcertWithAvailableSeats();
        concertRepository.save(concert);

        //when
        //then
        Assertions.assertThatThrownBy(() -> makeReservationService.reserve(new MakeReservationCommand(1L, 1L)))
                .isInstanceOf(EntityNotFoundException.class);

    }

    @Test
    void 선택좌석이_예약불가능하다면_예약할수없다() throws Exception {
        //given
        Concert concert = createConcertWithUnavailableSeats();
        User user = new User(null);
        userRepository.save(user);
        concertRepository.save(concert);

        //when
        //then
        Assertions.assertThatThrownBy(() -> makeReservationService.reserve(new MakeReservationCommand(1L, 1L)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 선택된 좌석입니다");
    }
}
