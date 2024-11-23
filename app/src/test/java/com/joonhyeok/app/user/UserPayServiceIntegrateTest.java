package com.joonhyeok.app.user;

import com.joonhyeok.app.concert.domain.Seat;
import com.joonhyeok.app.concert.domain.SeatRepository;
import com.joonhyeok.app.concert.domain.SeatStatus;
import com.joonhyeok.app.reservation.domain.Reservation;
import com.joonhyeok.app.reservation.domain.ReservationRepository;
import com.joonhyeok.app.reservation.domain.ReservationStatus;
import com.joonhyeok.app.user.application.OutboxService;
import com.joonhyeok.app.user.application.UserPayService;
import com.joonhyeok.app.user.application.dto.outbox.OutboxFindCommand;
import com.joonhyeok.app.user.application.dto.user.UserPayCommand;
import com.joonhyeok.app.user.application.dto.user.UserPayResult;
import com.joonhyeok.app.user.domain.outbox.Outbox;
import com.joonhyeok.app.user.domain.outbox.OutboxStatus;
import com.joonhyeok.app.user.domain.user.*;
import com.joonhyeok.app.user.infra.domain.outbox.PayOutboxKafkaListener;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;


@SpringBootTest
@ActiveProfiles("test")
@Sql("/ddl-test.sql")
@AutoConfigureEmbeddedDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "spring.kafka.consumer.group-id=test-group-${random.uuid}"
})
class UserPayServiceIntegrateTest {
    @Autowired
    private UserPayService userPayService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    @SpyBean
    private PayEventListener payEventListener;

    @SpyBean
    private PayOutboxKafkaListener payOutboxKafkaListener;

    @Autowired
    private OutboxService outboxService;

    @Test
    void 예약한유저가없는경우_결제_실패() {
        //given
        seatRepository.save(new Seat(null, SeatStatus.PENDING, null, 0L, 0));
        reservationRepository.save(new Reservation(ReservationStatus.RESERVED, 1L, 1L));

        //when
        UserPayCommand command = new UserPayCommand(1L, 1L);

        //then
        assertThatThrownBy(() -> userPayService.pay(command))
                .isInstanceOf(EntityNotFoundException.class);

    }

    @Test
    void 예약한좌석없는경우_결제_실패() {
        //given
        userRepository.save(new User(1L, new Account(0L, null), 0));
        reservationRepository.save(new Reservation(ReservationStatus.RESERVED, 1L, 1L));

        //when
        UserPayCommand command = new UserPayCommand(1L, 1L);

        //then
        assertThatThrownBy(() -> userPayService.pay(command))
                .isInstanceOf(EntityNotFoundException.class);

    }

    @Test
    void 에약내역이_없는경우_결제_실패() {
        //given
        seatRepository.save(new Seat(null, SeatStatus.PENDING, null, 0L, 0));
        userRepository.save(new User(1L, new Account(0L, null), 0));

        //when
        UserPayCommand command = new UserPayCommand(1L, 1L);

        //then
        assertThatThrownBy(() -> userPayService.pay(command))
                .isInstanceOf(EntityNotFoundException.class);

    }

    @Test
    void 에약내역과_유저가_일치하지않는경우_결제_실패() {
        //given
        seatRepository.save(new Seat(null, SeatStatus.PENDING, null, 0L, 0));
        userRepository.save(new User(null, new Account(0L, null), 0));
        userRepository.save(new User(null, new Account(0L, null), 0));
        reservationRepository.save(new Reservation(ReservationStatus.RESERVED, 1L, 1L));

        //when
        UserPayCommand command = new UserPayCommand(2L, 1L);

        //then
        assertThatThrownBy(() -> userPayService.pay(command))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @DisplayName("결제가 성공하면, 외부 데이터 플랫폼에 정보를 보낸다.")
    void pay() {
        //given
        seatRepository.save(new Seat(1L, SeatStatus.PENDING, null, 0L, 0));
        userRepository.save(new User(1L, new Account(0L, null), 0));
        reservationRepository.save(new Reservation(ReservationStatus.RESERVED, 1L, 1L));

        //when
        UserPayCommand command = new UserPayCommand(1L, 1L);
        UserPayResult result = userPayService.pay(command);

        //then
        then(payEventListener).should(times(1)).sendPayInfo(new PayEvent(result));
    }


    @Test
    @DisplayName("결제 성공시 카프카에 정보가 저장되는지 확인한다.")
    void payWithKafkaInfo() throws InterruptedException {
        //given
        seatRepository.save(new Seat(1L, SeatStatus.PENDING, null, 0L, 0));
        userRepository.save(new User(1L, new Account(0L, null), 0));
        reservationRepository.save(new Reservation(ReservationStatus.RESERVED, 1L, 1L));

        //when
        UserPayCommand command = new UserPayCommand(1L, 1L);
        userPayService.pay(command);

        //then
        await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> then(payOutboxKafkaListener).should(atMost(1)).listen(any(PayEvent.class)));

    }

    @Test
    @DisplayName("결제 성공시 카프카에 정보가 저장되는지 확인한다. - 저장된 결과는 SEND_SUCCESS")
    void payWithKafkaInfoStatus() throws InterruptedException {
        //given
        seatRepository.save(new Seat(1L, SeatStatus.PENDING, null, 0L, 0));
        userRepository.save(new User(1L, new Account(0L, null), 0));
        reservationRepository.save(new Reservation(ReservationStatus.RESERVED, 1L, 1L));

        //when
        UserPayCommand command = new UserPayCommand(1L, 1L);
        userPayService.pay(command);

        //then
        await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> then(payOutboxKafkaListener).should(atMost(1)).listen(any(PayEvent.class)));

        Outbox pay = outboxService.findOutboxById(new OutboxFindCommand("pay", 1L));
        assertThat(pay.getStatus()).isEqualTo(OutboxStatus.SEND_SUCCESS);
    }
}
