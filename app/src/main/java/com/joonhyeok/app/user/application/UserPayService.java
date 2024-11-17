package com.joonhyeok.app.user.application;

import com.joonhyeok.app.concert.domain.Seat;
import com.joonhyeok.app.concert.domain.SeatRepository;
import com.joonhyeok.app.queue.domain.Queue;
import com.joonhyeok.app.queue.domain.QueueRepository;
import com.joonhyeok.app.reservation.domain.Reservation;
import com.joonhyeok.app.reservation.domain.ReservationRepository;
import com.joonhyeok.app.user.application.dto.UserPayCommand;
import com.joonhyeok.app.user.application.dto.UserPayResult;
import com.joonhyeok.app.user.domain.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserPayService {
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final QueueRepository queueRepository;
    private final PayEventPublisher payEventPublisher;

    @Transactional
    public UserPayResult pay(UserPayCommand command) {
        Long userId = command.userId();
        Long reservationId = command.reservationId();

        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("해당 유저는 존재하지 않습니다. userId = " + userId)
        );

        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
                () -> new EntityNotFoundException("해당 예약 존재하지 않습니다. reservationId = " + reservationId)
        );

        Seat seat = seatRepository.findById(reservation.getSeatId()).orElseThrow(
                () -> new EntityNotFoundException("결제하려는 좌석이 존재하지 않습니다. seatId = " + reservation.getSeatId())
        );


        boolean isPayable = new PayValidator().validate(user, seat, reservation);

        if (!isPayable) {
            log.info("결제할 수 없는 좌석입니다. reservationId = {}", reservationId);
            throw new IllegalStateException("결제할 수 없는 좌석입니다. reservationId = " + reservationId);
        }

        user.usePoint(seat.getPrice());
        seat.paySeat();
        reservation.confirmPay();
        queueRepository.findByUserId(userId).ifPresent(Queue::expire);

        UserPayResult userPayResult = new UserPayResult(reservation.getId());
        payEventPublisher.publish(new PayEvent(userPayResult));

        return userPayResult;
    }
}
