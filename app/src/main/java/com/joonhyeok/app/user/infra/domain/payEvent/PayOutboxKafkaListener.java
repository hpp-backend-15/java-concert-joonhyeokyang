package com.joonhyeok.app.user.infra.domain.payEvent;

import com.joonhyeok.app.user.domain.PayEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayOutboxKafkaListener {
    private final OutboxService outboxService;

    @KafkaListener(topics = "#{'${spring.kafka.topic.names}'}", containerFactory = "payKafkaListenerContainerFactory")
    public void listen(PayEvent payEvent) {
        log.info("payEvent Outbox Listener received: {}", payEvent);
        Long relationalId = payEvent.result().reservationId();
        outboxService.updateSuccessStatus(new OutboxSendSuccessCommand("pay", relationalId));
    }
}
