package com.joonhyeok.app.reservation.infra;

import com.joonhyeok.app.reservation.domain.Reservation;
import com.joonhyeok.app.reservation.domain.ReservationRepository;
import com.joonhyeok.app.reservation.domain.ReservationTimeOutPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.joonhyeok.app.reservation.domain.ReservationStatus.RESERVED;

@Service
@RequiredArgsConstructor
public class Reservation5MinuteTimeOutPolicy implements ReservationTimeOutPolicy {
    private final ReservationRepository reservationRepository;

    /*
    * 5분 지난 예약자들 모두 Cancelled 상태로 변경
    */
    @Override
    public void invalidate() {
        reservationRepository.findAllByStatusAndCreatedAtAfter(RESERVED, LocalDateTime.now().plusMinutes(5))
                .forEach(Reservation::invalidate);
    }
}
