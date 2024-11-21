package com.joonhyeok.app.common.scheduler;

import com.joonhyeok.app.reservation.application.Reservation5MinuteTimeOutPolicy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationPolicyScheduler {
    private final Reservation5MinuteTimeOutPolicy reservationPolicy;

    @Scheduled(fixedDelay = 300_000)
    public void expire() {
        reservationPolicy.invalidate();
    }
}
