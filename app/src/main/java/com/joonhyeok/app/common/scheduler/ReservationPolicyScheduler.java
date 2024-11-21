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
    public static final int FIVE_MINUTE = 5 * 60 * 1000;
    private final Reservation5MinuteTimeOutPolicy reservationPolicy;

    @Scheduled(fixedDelay = FIVE_MINUTE)
    public void expire() {
        reservationPolicy.invalidate();
    }
}
