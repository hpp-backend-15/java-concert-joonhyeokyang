package com.joonhyeok.app.user.infra.domain.payEvent;

import com.joonhyeok.app.user.domain.PayEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
public class PayOutboxKafkaListener {
    private final OutboxRepository outBoxRepository;

    @Transactional
    @KafkaListener(topics = "#{'${spring.kafka.topic.names}'}", containerFactory = "payKafkaListenerContainerFactory")
    public void listen(PayEvent payEvent) {
        Outbox outBox = outBoxRepository.findOutboxByTypeAndRelationalId("pay", payEvent.result().reservationId())
                .orElseThrow(() -> new EntityNotFoundException("Outbox에 등록되지 않은 이벤트입니다. >>> {}"));

        outBox.changeStatusToSuccess();
    }
}
