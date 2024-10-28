package com.joonhyeok.app.reservation.infra;

import com.joonhyeok.app.reservation.domain.ReservationTimeOutPolicy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationPolicyScheduler {
    private final ReservationTimeOutPolicy reservationPolicy;

    @Scheduled(fixedDelay = 30000)
    public void expire() {
        reservationPolicy.invalidate();
    }
}
