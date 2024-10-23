package com.joonhyeok.app.concert;

import com.joonhyeok.app.concert.domain.Concert;
import com.joonhyeok.app.concert.domain.PerformanceDate;
import com.joonhyeok.app.concert.domain.Seat;
import com.joonhyeok.app.concert.domain.SeatStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class ConcertTest {

    @Test
    void 한좌석이라도예약가능하다면_예약가능한_날짜기준으로조회가_가능하다() {
        //given
        Concert concert = createConcertWithAvailableSeats();

        //when
        List<PerformanceDate> availablePerformanceDates = concert.getAvailablePerformanceDates();

        //then
        Assertions.assertThat(availablePerformanceDates).hasSize(3);

    }

    @Test
    void 모든좌석이예약불가능하다면_예약가능한_날짜기준으로조회결과는_0이다() {
        //given
        Concert concert = createConcertWithUnavailableSeats();

        //when
        List<PerformanceDate> availablePerformanceDates = concert.getAvailablePerformanceDates();

        //then
        Assertions.assertThat(availablePerformanceDates).isEmpty();
    }

    @Test
    void 예약가능한날의_예약가능한_좌석을조회_할수있다() {
        //given
        Concert concert = createConcertWithAvailableSeats();

        //when
        List<Seat> seats = concert.getAvailableSeatByPerformanceDate(1L);

        //then
        Assertions.assertThat(seats).hasSize(50);
    }

    @Test
    void 예약불가능한날의_예약불가능한_좌석을조회_할수있다() {
        //given
        Concert concert = createConcertWithUnavailableSeats();

        //when
        List<Seat> seats = concert.getUnavailableSeatByPerformanceDate(1L);

        //then
        Assertions.assertThat(seats).hasSize(50);
    }

    private static Concert createConcertWithAvailableSeats() {
        List<PerformanceDate> performanceDates = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            PerformanceDate performanceDate = new PerformanceDate((long) i, LocalDate.now().plusDays(i), createAllAvailableSeats());
            performanceDates.add(performanceDate);
        }
        return new Concert(0L, "joonhyeok", performanceDates);
    }

    private static Concert createConcertWithUnavailableSeats() {
        List<PerformanceDate> performanceDates = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            PerformanceDate performanceDate = new PerformanceDate((long) i, LocalDate.now().plusDays(i), createAllUnavailableSeats());
            performanceDates.add(performanceDate);
        }
        return new Concert(0L, "joonhyeok", performanceDates);
    }

    private static List<Seat> createAllAvailableSeats() {
        List<Seat> seats = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Seat seat = new Seat((long) i, SeatStatus.AVAILABLE, null, 0L, 0);
            seats.add(seat);
        }
        return seats;
    }

    private static List<Seat> createAllUnavailableSeats() {
        List<Seat> seats = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Seat seat = new Seat((long) i, SeatStatus.UNAVAILABLE, null, 0L, 0);
            seats.add(seat);
        }
        return seats;
    }
}
