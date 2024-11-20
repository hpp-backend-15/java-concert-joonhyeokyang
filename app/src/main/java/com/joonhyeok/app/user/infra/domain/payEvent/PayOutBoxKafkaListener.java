package com.joonhyeok.app.user.infra.domain.payEvent;

import com.joonhyeok.app.user.domain.PayEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
public class PayOutBoxKafkaListener {
    private final OutBoxRepository outBoxRepository;

    @Transactional
    @KafkaListener(topics = "#{'${spring.kafka.topic.names}'}", containerFactory = "payKafkaListenerContainerFactory")
    public void listen(PayEvent payEvent) {
        OutBox outBox = outBoxRepository.findOutBoxByTypeAndRelationalId("pay", payEvent.result().reservationId())
                .orElseThrow(() -> new EntityNotFoundException("OutBox에 등록되지 않은 이벤트입니다. >>> {}"));

        outBox.changeStatusToSuccess();
    }
}
