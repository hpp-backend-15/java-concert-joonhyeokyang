package com.joonhyeok.app.reservation.application;

import com.joonhyeok.app.concert.domain.Concert;
import com.joonhyeok.app.concert.domain.Seat;
import com.joonhyeok.app.concert.domain.SeatStatus;
import com.joonhyeok.app.reservation.domain.Reservation;
import com.joonhyeok.app.reservation.domain.ReservationStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.joonhyeok.app.concert.domain.ConcertTestHelper.*;
import static com.joonhyeok.app.reservation.domain.ReservationStatus.RESERVED;

public class MakeReservationServiceTest {

    @Test
    void 좌석이_예약불가능한상태라면_예약할수없다() throws Exception {
        MakeReservationService makeReservationService = new MakeReservationService();

        //given
        Concert concert = createConcert(createAllUnavailableSeats());
        concertRepository.save(concert);

        //when
        MakeReservationResult result = makeReservationService.reserve(new MakeReservationCommand(0L, 0L, 0L, 0L));

        //then
        Reservation reservation = reservationRepository.findById(result.reservationId());
        Assertions.assertThat(reservation).isNotNull();
        Assertions.assertThat(reservation.getStatus()).isEqualTo(RESERVED);

        Seat seat = seatRepository.findById(reservation.getSeat().getId());
        Assertions.assertThat(seat).isNotNull();
        Assertions.assertThat(seat.getStatus()).isEqualTo(SeatStatus.PENDING);



    }
    // 유저가 없는 경우
}
