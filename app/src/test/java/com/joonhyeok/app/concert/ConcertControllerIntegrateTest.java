package com.joonhyeok.app.concert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joonhyeok.app.concert.domain.Concert;
import com.joonhyeok.app.concert.domain.ConcertRepository;
import com.joonhyeok.app.queue.domain.Queue;
import com.joonhyeok.app.queue.domain.QueueRepository;
import com.joonhyeok.app.queue.domain.QueueStatus;
import com.joonhyeok.openapi.models.FindConcertPerformanceDatesResponse;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;

import static com.joonhyeok.app.concert.ConcertTestHelper.createConcertWithAvailableSeats;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql("/ddl-test.sql")
@AutoConfigureEmbeddedDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ConcertControllerIntegrateTest {

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    private static final String BASE_URL = "/concert";

    @Test
    void 적절한_토큰으로_예약가능일자를_조회성공한다() throws Exception {
        //given
        Concert concert = createConcertWithAvailableSeats();
        Concert saved = concertRepository.save(concert);
        Queue queue = new Queue(null, "waitId", 1L, QueueStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now().plusMinutes(10), null);
        queueRepository.save(queue);

        //when
        ResultActions resultActions = mvc.perform(
                get(BASE_URL + "/{concertId}/performanceDates", saved.getId())
                        .header("Wait-Token", "waitId")
        );

        //then
        resultActions.andExpect(status().isOk());
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        FindConcertPerformanceDatesResponse findConcertPerformanceDatesResponse =
                objectMapper.readValue(responseBody, FindConcertPerformanceDatesResponse.class);
        Assertions.assertThat(findConcertPerformanceDatesResponse.getAvailablePerformanceDates().size()).isEqualTo(3);
    }


    @Test
    void 대기중_토큰으로_예약가능일자를_조회하면_실패한다() throws Exception {
        //given
        Concert concert = createConcertWithAvailableSeats();
        Concert saved = concertRepository.save(concert);
        Queue queue = new Queue(null, "waitId", 1L, QueueStatus.WAIT, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now().plusMinutes(10), null);
        queueRepository.save(queue);

        //when
        //then
        mvc.perform(
                        get(BASE_URL + "/{concertId}/performanceDates", saved.getId())
                                .header("Wait-Token", "waitId"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void 대기중_토큰으로_기다린뒤_예약가능일자를_조회하면_성공한다() throws Exception {
        //given
        Concert concert = createConcertWithAvailableSeats();
        Concert saved = concertRepository.save(concert);
        Queue queue = new Queue(null, "waitId", 1L, QueueStatus.WAIT, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now().plusMinutes(10), null);
        queueRepository.save(queue);

        Thread.sleep(1500);
        //when
        //then
        mvc.perform(
                        get(BASE_URL + "/{concertId}/performanceDates", saved.getId())
                                .header("Wait-Token", "waitId"))
                .andExpect(status().isOk());
    }

    @Test
    void 만료된_토큰으로_예약가능일자를_조회하면_실패한다() throws Exception {
        //given
        Concert concert = createConcertWithAvailableSeats();
        Concert saved = concertRepository.save(concert);
        Queue queue = new Queue(null, "waitId", 1L, QueueStatus.EXPIRED, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now().plusMinutes(10), null);
        queueRepository.save(queue);

        //when
        //then
        mvc.perform(
                        get(BASE_URL + "/{concertId}/performanceDates", saved.getId())
                                .header("Wait-Token", "waitId"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void 토큰없이_예약가능일자를_조회하면_실패한다() throws Exception {
        //given
        Concert concert = createConcertWithAvailableSeats();
        Concert saved = concertRepository.save(concert);
        Queue queue = new Queue(null, "waitId", 1L, QueueStatus.EXPIRED, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now().plusMinutes(10), null);
        queueRepository.save(queue);

        //when
        //then
        mvc.perform(
                        get(BASE_URL + "/{concertId}/performanceDates", saved.getId()))
                .andExpect(status().is4xxClientError());
    }
}
