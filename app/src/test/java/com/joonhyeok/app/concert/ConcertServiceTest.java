package com.joonhyeok.app.concert;

import com.joonhyeok.app.concert.application.ConcertService;
import com.joonhyeok.app.concert.application.dto.AvailablePerformanceDatesQuery;
import com.joonhyeok.app.concert.application.dto.AvailableSeatsByDateQuery;
import com.joonhyeok.app.concert.application.dto.PerformanceDatesQueryResult;
import com.joonhyeok.app.concert.application.dto.SeatsQueryResult;
import com.joonhyeok.app.concert.domain.*;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class ConcertServiceTest {
    ConcertService concertService;
    ConcertRepository concertRepository;

    @BeforeEach
    void setUp() {
        concertRepository = new ConcertMemoryRepository(new SeatMemoryRepository());
        concertService = new ConcertService(concertRepository);
    }


    @Test
    void 예약가능한일자를_조회할수있다() {
        //given
        Concert concert = createConcertWithAvailableSeats();
        concertRepository.save(concert);
        AvailablePerformanceDatesQuery datesQuery = new AvailablePerformanceDatesQuery(1L);
        //when
        PerformanceDatesQueryResult performanceDatesQueryResult = concertService.queryPerformanceDates(datesQuery);

        //then
        Assertions.assertThat(performanceDatesQueryResult.availableDates()).hasSize(3);

    }

    @Test
    void 예약_불가능한일자를_조회할수없다() {
        //given
        Concert concert = createConcertWithUnavailableSeats();
        concertRepository.save(concert);
        AvailablePerformanceDatesQuery datesQuery = new AvailablePerformanceDatesQuery(1L);

        //when
        PerformanceDatesQueryResult performanceDatesQueryResult = concertService.queryPerformanceDates(datesQuery);

        //then
        Assertions.assertThat(performanceDatesQueryResult.availableDates()).isEmpty();
        Assertions.assertThat(performanceDatesQueryResult.unavailableDates()).hasSize(3);

    }

    @Test
    void 예약가능한_좌석을_조회할수있다() {
        //given
        Concert concert = createConcertWithAvailableSeats();
        concertRepository.save(concert);
        AvailableSeatsByDateQuery query = new AvailableSeatsByDateQuery(1L, 1L);

        //when
        SeatsQueryResult seatsQueryResult = concertService.querySeatsByDate(query);

        //then
        Assertions.assertThat(seatsQueryResult.availableSeats()).hasSize(50);
        Assertions.assertThat(seatsQueryResult.unavailableSeats()).isEmpty();
    }

    @Test
    void 예약불가능한_좌석을_조회할수없다() {
        //given
        Concert concert = createConcertWithUnavailableSeats();
        concertRepository.save(concert);
        AvailableSeatsByDateQuery query = new AvailableSeatsByDateQuery(1L, 1L);

        //when
        SeatsQueryResult seatsQueryResult = concertService.querySeatsByDate(query);

        //then
        Assertions.assertThat(seatsQueryResult.availableSeats()).isEmpty();
        Assertions.assertThat(seatsQueryResult.unavailableSeats()).hasSize(50);
    }

    @Test
    void 예약불가능한_콘서트Id의_좌석을_조회할수없다() {
        //given
        Concert concert = createConcertWithAvailableSeats();
        concertRepository.save(concert);
        AvailableSeatsByDateQuery query = new AvailableSeatsByDateQuery(4L, 1L);

        //when
        //then
        Assertions.assertThatThrownBy(() -> concertService.querySeatsByDate(query)).isInstanceOf(EntityNotFoundException.class);

    }

    @Test
    void 예약불가능한_peformanceDateId의_좌석을_조회할수없다() {
        //given
        Concert concert = createConcertWithAvailableSeats();
        concertRepository.save(concert);
        AvailableSeatsByDateQuery query = new AvailableSeatsByDateQuery(1L, 4L);

        //when
        //then
        Assertions.assertThatThrownBy(() -> concertService.querySeatsByDate(query)).isInstanceOf(EntityNotFoundException.class);
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
