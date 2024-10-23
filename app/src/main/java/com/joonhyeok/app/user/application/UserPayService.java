package com.joonhyeok.app.user.application;

import com.joonhyeok.app.concert.domain.Seat;
import com.joonhyeok.app.concert.domain.SeatRepository;
import com.joonhyeok.app.queue.domain.Queue;
import com.joonhyeok.app.queue.domain.QueueRepository;
import com.joonhyeok.app.reservation.domain.Reservation;
import com.joonhyeok.app.reservation.domain.ReservationRepository;
import com.joonhyeok.app.user.application.dto.UserPayCommand;
import com.joonhyeok.app.user.application.dto.UserPayResult;
import com.joonhyeok.app.user.domain.PayValidator;
import com.joonhyeok.app.user.domain.User;
import com.joonhyeok.app.user.domain.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserPayService {
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final QueueRepository queueRepository;
    private final PayValidator payValidator;

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


        boolean isPayable = payValidator.validate(user, seat, reservation);

        //TODO 코치님께 여쭤봐서 아래 구문을 이벤트로 처리하는 게 의미 있는지 확인..?
        if (isPayable) {
            user.usePoint(seat.getPrice());
            seat.confirmPay();
            reservation.confirmPay();
            queueRepository.findByUserId(userId).ifPresent(Queue::expire);
        }
        return new UserPayResult(reservation.getId());
    }
}
