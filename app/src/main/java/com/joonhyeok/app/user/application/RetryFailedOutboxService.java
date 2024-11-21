package com.joonhyeok.app.user.application;

import com.joonhyeok.app.user.domain.outbox.Outbox;
import com.joonhyeok.app.user.domain.outbox.OutboxRepository;
import com.joonhyeok.app.user.domain.outbox.OutboxStatus;
import com.joonhyeok.app.user.domain.user.PayEvent;
import com.joonhyeok.app.user.domain.user.PayEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RetryFailedOutboxService {
    private final OutboxRepository outBoxRepository;
    private final PayEventPublisher payEventPublisher;


    @Transactional(readOnly = true)
    public void execute() {
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        List<OutboxStatus> statuses = Arrays.asList(OutboxStatus.INIT, OutboxStatus.SEND_FAIL);

        List<Outbox> outboxes = outBoxRepository.findByCreatedAtBeforeAndTypeAndStatusIn(fiveMinutesAgo, "pay", statuses);

        for (Outbox outbox : outboxes) {
            PayEvent payEvent = PayEvent.from(outbox);
            payEventPublisher.publish(payEvent);
        }
    }
}
