package com.joonhyeok.app.common.scheduler;

import com.joonhyeok.app.queue.appication.QueueFixedTotalPolicy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueuePolicyScheduler {
    public static final int ONE_MINUTE = 60 * 1000;
    public static final int FIVE_MINUTE = 5 * 60 * 1000;

    private final QueueFixedTotalPolicy queuePolicy;

    @Scheduled(fixedDelay = ONE_MINUTE)
    public void activate() {
        log.debug("activate queuePolicy");
        queuePolicy.activate();
    }

    @Scheduled(fixedDelay = FIVE_MINUTE)
    public void expire() {
        log.debug("expire queuePolicy");
        queuePolicy.expire();
    }
}
