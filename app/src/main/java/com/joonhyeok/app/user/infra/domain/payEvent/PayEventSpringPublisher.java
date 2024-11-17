package com.joonhyeok.app.user.infra.domain.payEvent;

import com.joonhyeok.app.user.domain.PayEvent;
import com.joonhyeok.app.user.domain.PayEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PayEventSpringPublisher implements PayEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(PayEvent payEvent) {
        log.info("publish pay event: {}", payEvent);
        eventPublisher.publishEvent(payEvent);
    }
}
