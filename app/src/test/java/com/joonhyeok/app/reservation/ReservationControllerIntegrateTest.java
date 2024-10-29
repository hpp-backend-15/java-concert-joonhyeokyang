package com.joonhyeok.app.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joonhyeok.app.concert.domain.Concert;
import com.joonhyeok.app.concert.domain.ConcertRepository;
import com.joonhyeok.app.queue.domain.Queue;
import com.joonhyeok.app.queue.domain.QueueRepository;
import com.joonhyeok.app.queue.domain.QueueStatus;
import com.joonhyeok.app.user.domain.Account;
import com.joonhyeok.app.user.domain.User;
import com.joonhyeok.app.user.domain.UserRepository;
import com.joonhyeok.openapi.models.MakeReservationRequest;
import com.joonhyeok.openapi.models.RegisterQueueRequest;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.joonhyeok.app.concert.ConcertTestHelper.createConcertWithAvailableSeats;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql("/ddl-test.sql")
@ActiveProfiles("test")
@AutoConfigureEmbeddedDatabase
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
class ReservationControllerIntegrateTest {
    @Autowired
    ConcertRepository concertRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    QueueRepository queueRepository;

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    private static final String BASE_URL = "/reservations";


    int numberOfThreads = 300;
    List<Integer> executionOrder;
    ExecutorService executorService;
    CountDownLatch latch; // 스레드 종료를 대기하기 위한 Latch

    @BeforeEach
    void setUp() {
        executorService = Executors.newFixedThreadPool(16);
        executionOrder = new ArrayList<>();
        latch = new CountDownLatch(numberOfThreads);
    }

    @Test
    void 한_유저가_한_좌석을_동시에_300번_예약시도한다_그러나_예약한사람은_오직한명이다() throws Exception {
        //given
        Concert concert = createConcertWithAvailableSeats();
        User user = new User(null, new Account(0L, LocalDateTime.now()), 0);
        queueRepository.save(new Queue(1L, "1", 1L, QueueStatus.ACTIVE, LocalDateTime.now(), null, null, null));
        userRepository.save(user);
        concertRepository.save(concert);

        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger fail = new AtomicInteger(0);

        //when
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    ResultActions perform = mvc.perform(
                            post(BASE_URL)
                                    .header("Wait-Token", "1")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(new MakeReservationRequest().userId(1L).seatId(1L)))
                    );
                    if (perform.andReturn().getResponse().getStatus() == 201) {
                        success.incrementAndGet();
                    } else fail.incrementAndGet();

                } catch (Exception e) {
                    fail.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    latch.countDown();  // 작업이 끝난 후 카운트 감소
                }
            });
        }

        latch.await();  // 모든 스레드가 끝날 때까지 대기

        //then
        assertThat(success.get()).isEqualTo(1);
        assertThat(fail.get()).isEqualTo(299);
    }

    @Test
    void 존재하지_않는_유저는_대기열등록이_불가능하다() throws Exception {
        //given


        //when
        ResultActions resultActions = mvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterQueueRequest().userId(-1L)))
        );

        //then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    void 이미_대기하는_유저는_대기열등록이_불가능하다() throws Exception {
        //given
        userRepository.save(new User(1L, new Account(0L, null), 0));

        mvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterQueueRequest().userId(1L)))
        );

        //when
        ResultActions resultActions = mvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterQueueRequest().userId(1L)))
        );

        //then
        resultActions
                .andExpect(status().isConflict());
    }

    @Test
    void 대기열에_등록된_유저를_조회할수있다() throws Exception {
        //given
        userRepository.save(new User(1L, new Account(0L, null), 0));
        ResultActions registerResult = mvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterQueueRequest().userId(1L)))
        );

        String waitId = registerResult.andReturn().getResponse().getHeader("Wait-Token");


        //when
        ResultActions resultActions = mvc.perform(
                get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Wait-Token", waitId)
        );


        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.positionInQueue").value(1))
                .andExpect(jsonPath("$.waitId").value(waitId))
                .andExpect(jsonPath("$.status").value(QueueStatus.WAIT.name()));
    }

    @Test
    void 대기열에_ACTIVE된_유저를_조회하는경우_status는_ACTIVE() throws Exception {
        //given
        userRepository.save(new User(1L, new Account(0L, null), 0));
        ResultActions registerResult = mvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterQueueRequest().userId(1L)))
        );

        String waitId = registerResult.andReturn().getResponse().getHeader("Wait-Token");
        Thread.sleep(1200);


        //when
        ResultActions resultActions = mvc.perform(
                get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Wait-Token", waitId)
        );


        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.positionInQueue").value(0))
                .andExpect(jsonPath("$.waitId").value(waitId))
                .andExpect(jsonPath("$.status").value(QueueStatus.ACTIVE.name()));

    }


    @Test
    void 대기열에_존재하지않는_waitId를_조회할수없다() throws Exception {
        //given
        //when
        ResultActions resultActions = mvc.perform(
                get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Wait-Token", "invalidRandomWaitToken")
        );

        //then
        resultActions.andExpect(status().isBadRequest());
    }

}
