package com.joonhyeok.app.concert.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static com.joonhyeok.app.concert.domain.ConcertTestHelper.*;

public class ConcertTest {

    @Test
    void 한좌석이라도예약가능하다면_예약가능한_날짜기준으로조회가_가능하다() throws Exception {
        //given
        Concert concert = createConcert(createAllAvailableSeats());

        //when
        List<PerformanceDate> availablePerformanceDates = concert.getAvailablePerformanceDates();

        //then
        Assertions.assertThat(availablePerformanceDates).hasSize(3);

    }

    @Test
    void 모든좌석이예약불가능하다면_예약가능한_날짜기준으로조회결과는_0이다() throws Exception {
        //given
        Concert concert = createConcert(createAllUnavailableSeats());

        //when
        List<PerformanceDate> availablePerformanceDates = concert.getAvailablePerformanceDates();

        //then
        Assertions.assertThat(availablePerformanceDates).hasSize(0);
    }

    @Test
    void 예약가능한날의_예약가능한_좌석을조회_할수있다() throws Exception {
        //given
        Concert concert = createConcert(createAllAvailableSeats());

        //when
        List<Seat> seats = concert.getAvailableSeatByPerformanceDate(0L);

        //then
        Assertions.assertThat(seats).hasSize(50);
    }

    @Test
    void 예약불가능한날의_예약불가능한_좌석을조회_할수있다() throws Exception {
        //given
        Concert concert = createConcert(createAllUnavailableSeats());

        //when
        List<Seat> seats = concert.getUnavailableSeatByPerformanceDate(0L);

        //then
        Assertions.assertThat(seats).hasSize(50);
    }

}
