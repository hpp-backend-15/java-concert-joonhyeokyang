package com.joonhyeok.app.common.scheduler;

import com.joonhyeok.app.user.application.RetryFailedOutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxFailRetryScheduler {
    public static final int FIVE_MINUTE = 5 * 60 * 1000;
    private final RetryFailedOutboxService retryFailedOutboxService;

    @Scheduled(fixedDelay = FIVE_MINUTE)
    public void retry() {
        log.info("retrying failed outbox...");
        retryFailedOutboxService.execute();
    }
}
