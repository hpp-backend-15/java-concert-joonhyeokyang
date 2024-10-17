package com.joonhyeok.app.concert.domain;

import com.joonhyeok.app.concert.application.ConcertService;
import com.joonhyeok.app.concert.application.dto.AvailablePerformanceDatesQuery;
import com.joonhyeok.app.concert.application.dto.PerformanceDatesQueryResult;
import com.joonhyeok.app.concert.infra.ConcertMemoryRepository;
import com.joonhyeok.app.concert.infra.SeatMemoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.joonhyeok.app.concert.domain.ConcertTestHelper.*;

public class ConcertServiceTest {
    ConcertService concertService;
    ConcertRepository concertRepository;

    @BeforeEach
    void setUp() {
        concertRepository = new ConcertMemoryRepository(new SeatMemoryRepository());
        concertService = new ConcertService(concertRepository);
    }


    @Test
    void 예약가능한일자를_조회할수있다() throws Exception {
        //given
        Concert concert = createConcert(createAllAvailableSeats());
        concertRepository.save(concert);
        AvailablePerformanceDatesQuery datesQuery = new AvailablePerformanceDatesQuery(0L);
        //when
        PerformanceDatesQueryResult performanceDatesQueryResult = concertService.queryPerformanceDates(datesQuery);

        //then
        Assertions.assertThat(performanceDatesQueryResult.availableDates()).hasSize(3);

    }
    @Test
    void 예약_불가능한일자를_조회할수없다() throws Exception {
        //given
        Concert concert = createConcert(createAllUnavailableSeats());
        concertRepository.save(concert);
        AvailablePerformanceDatesQuery datesQuery = new AvailablePerformanceDatesQuery(0L);

        //when
        //then
        Assertions.assertThatThrownBy(() -> concertService.queryPerformanceDates(datesQuery))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
