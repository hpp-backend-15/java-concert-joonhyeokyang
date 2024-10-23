package com.joonhyeok.app.reservation;

import com.joonhyeok.app.concert.domain.Concert;
import com.joonhyeok.app.concert.domain.ConcertRepository;
import com.joonhyeok.app.reservation.application.MakeReservationService;
import com.joonhyeok.app.reservation.application.dto.MakeReservationCommand;
import com.joonhyeok.app.user.domain.Account;
import com.joonhyeok.app.user.domain.User;
import com.joonhyeok.app.user.domain.UserRepository;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.joonhyeok.app.concert.ConcertTestHelper.createConcertWithAvailableSeats;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Sql("/ddl-test.sql")
@AutoConfigureEmbeddedDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MakeReservationServiceConcurrentTest {

    @Autowired
    ConcertRepository concertRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MakeReservationService makeReservationService;

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
        userRepository.save(user);
        concertRepository.save(concert);

        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger fail = new AtomicInteger(0);
        //when
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    //when
                    makeReservationService.reserve(new MakeReservationCommand(1L, 1L));
                    success.incrementAndGet();
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
    void 여러_유저가_한_좌석을_동시에_총300번_예약시도한다_그러나_예약한사람은_오직한명이다() throws Exception {
        //given
        Concert concert = createConcertWithAvailableSeats();
        concertRepository.save(concert);

        for (int i = 0; i < 300; i++) {
            User user = new User(null, new Account(0L, LocalDateTime.now()), 0);
            userRepository.save(user);
        }

        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger fail = new AtomicInteger(0);
        //when
        for (int i = 1; i <= numberOfThreads; i++) {
            int finalI1 = i;
            executorService.submit(() -> {
                try {
                    //when
                    makeReservationService.reserve(new MakeReservationCommand(1L, (long) (finalI1)));
                    success.incrementAndGet();
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
    void 여러_유저가_여러_좌석을_동시에_예약시도한다_그러나_예약에성공된좌석과_사람수는같다() throws Exception {
        //given
        Concert concert = createConcertWithAvailableSeats();
        concertRepository.save(concert);

        for (int i = 0; i < 300; i++) {
            User user = new User(null, new Account(0L, LocalDateTime.now()), 0);
            userRepository.save(user);
        }

        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger fail = new AtomicInteger(0);
        //when
        for (int i = 1; i <= numberOfThreads; i++) {
            int finalI1 = i;
            executorService.submit(() -> {
                try {
                    //when
                    makeReservationService.reserve(new MakeReservationCommand((long) (finalI1 % 50), (long) (finalI1)));
                    success.incrementAndGet();
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
        assertThat(success.get()).isLessThanOrEqualTo(50);
        assertThat(fail.get()).isGreaterThanOrEqualTo(250);
    }
}
