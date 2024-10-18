package com.joonhyeok.app.queue.infra;

import com.joonhyeok.app.queue.domain.QueuePolicy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueuePolicyScheduler {
    private final QueuePolicy queuePolicy;

    @Scheduled(fixedDelay = 5000)
    public void activate() {
        queuePolicy.activate();
    }

    @Scheduled(fixedDelay = 7000)
    public void expire() {
        queuePolicy.expire();
    }
}
