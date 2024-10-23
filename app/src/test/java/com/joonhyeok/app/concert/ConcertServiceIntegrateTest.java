package com.joonhyeok.app.concert;

import com.joonhyeok.app.concert.application.ConcertService;
import com.joonhyeok.app.concert.application.dto.AvailablePerformanceDatesQuery;
import com.joonhyeok.app.concert.application.dto.AvailableSeatsByDateQuery;
import com.joonhyeok.app.concert.application.dto.PerformanceDatesQueryResult;
import com.joonhyeok.app.concert.application.dto.SeatsQueryResult;
import com.joonhyeok.app.concert.domain.Concert;
import com.joonhyeok.app.concert.domain.ConcertRepository;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import static com.joonhyeok.app.concert.ConcertTestHelper.createConcertWithAvailableSeats;
import static com.joonhyeok.app.concert.ConcertTestHelper.createConcertWithUnavailableSeats;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.BEFORE_EACH_TEST_METHOD;

@SpringBootTest
@ActiveProfiles("test")
@Sql("/ddl-test.sql")
@AutoConfigureEmbeddedDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ConcertServiceIntegrateTest {
    @Autowired
    ConcertService concertService;

    @Autowired
    ConcertRepository concertRepository;


    @Test
    void 예약가능한일자를_조회할수있다() throws Exception {
        //given
        Concert concert = createConcertWithAvailableSeats();
        Concert saved = concertRepository.save(concert);
        AvailablePerformanceDatesQuery datesQuery = new AvailablePerformanceDatesQuery(1L);

        //when
        PerformanceDatesQueryResult performanceDatesQueryResult = concertService.queryPerformanceDates(datesQuery);

        //then
        Assertions.assertThat(performanceDatesQueryResult.availableDates()).hasSize(3);

    }

    @Test
    void 예약_불가능한일자를_조회할수없다() throws Exception {
        //given
        Concert concert = createConcertWithUnavailableSeats();
        concertRepository.save(concert);
        AvailablePerformanceDatesQuery datesQuery = new AvailablePerformanceDatesQuery(4L);

        //when
        //then
        Assertions.assertThatThrownBy(() -> concertService.queryPerformanceDates(datesQuery))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void 예약가능한_좌석을_조회할수있다() throws Exception {
        //given
        Concert concert = createConcertWithAvailableSeats();
        concertRepository.save(concert);
        AvailableSeatsByDateQuery query = new AvailableSeatsByDateQuery(1L, 1L);

        //when
        SeatsQueryResult seatsQueryResult = concertService.querySeatsByDate(query);

        //then
        Assertions.assertThat(seatsQueryResult.availableSeats()).hasSize(50);
        Assertions.assertThat(seatsQueryResult.unavailableSeats()).hasSize(0);
    }

    @Test
    void 예약불가능한_좌석을_조회할수없다() throws Exception {
        //given
        Concert concert = createConcertWithUnavailableSeats();
        concertRepository.save(concert);
        AvailableSeatsByDateQuery query = new AvailableSeatsByDateQuery(1L, 1L);

        //when
        SeatsQueryResult seatsQueryResult = concertService.querySeatsByDate(query);

        //then
        Assertions.assertThat(seatsQueryResult.availableSeats()).hasSize(0);
        Assertions.assertThat(seatsQueryResult.unavailableSeats()).hasSize(50);
    }

    @Test
    void 예약불가능한_콘서트Id의_좌석을_조회할수없다() throws Exception {
        //given
        Concert concert = createConcertWithAvailableSeats();
        concertRepository.save(concert);
        AvailableSeatsByDateQuery query = new AvailableSeatsByDateQuery(4L, 1L);

        //when
        //then
        Assertions.assertThatThrownBy(() -> concertService.querySeatsByDate(query)).isInstanceOf(EntityNotFoundException.class);

    }

    @Test
    void 예약불가능한_peformanceDateId의_좌석을_조회할수없다() throws Exception {
        //given
        Concert concert = createConcertWithAvailableSeats();
        concertRepository.save(concert);
        AvailableSeatsByDateQuery query = new AvailableSeatsByDateQuery(1L, 4L);

        //when
        //then
        Assertions.assertThatThrownBy(() -> concertService.querySeatsByDate(query)).isInstanceOf(EntityNotFoundException.class);
    }

}
