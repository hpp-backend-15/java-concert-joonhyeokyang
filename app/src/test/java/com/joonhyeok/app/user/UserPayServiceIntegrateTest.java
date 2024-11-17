package com.joonhyeok.app.user;

import com.joonhyeok.app.concert.domain.Seat;
import com.joonhyeok.app.concert.domain.SeatRepository;
import com.joonhyeok.app.concert.domain.SeatStatus;
import com.joonhyeok.app.reservation.domain.Reservation;
import com.joonhyeok.app.reservation.domain.ReservationRepository;
import com.joonhyeok.app.reservation.domain.ReservationStatus;
import com.joonhyeok.app.user.application.UserPayService;
import com.joonhyeok.app.user.application.dto.UserPayCommand;
import com.joonhyeok.app.user.application.dto.UserPayResult;
import com.joonhyeok.app.user.domain.*;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;


@SpringBootTest
@ActiveProfiles("test")
@Sql("/ddl-test.sql")
@AutoConfigureEmbeddedDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private PayExternalKafkaListener payExternalKafkaListener;

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
        then(payEventListener).should(BDDMockito.times(1)).sendPayInfo(new PayEvent(result));
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
        UserPayResult result = userPayService.pay(command);
        PayEvent payEvent = new PayEvent(result);

        //then
        payExternalKafkaListener.getLatch().await(3, TimeUnit.SECONDS);
        assertThat(payExternalKafkaListener.getReceived()).isEqualTo(payEvent);
    }
}
