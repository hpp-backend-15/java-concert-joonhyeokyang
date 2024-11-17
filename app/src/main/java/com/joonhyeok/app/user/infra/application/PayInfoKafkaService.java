package com.joonhyeok.app.user.infra.application;

import com.joonhyeok.app.user.application.PayInfoService;
import com.joonhyeok.app.user.domain.PayEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Primary
@Service
@Slf4j
@RequiredArgsConstructor
public class PayInfoKafkaService implements PayInfoService {

    @Value("${spring.kafka.topic.names}")
    private String payTopicName;

    private final KafkaTemplate<String, PayEvent> kafkaTemplate;

    @Override
    public void sendPayInfo(PayEvent payEvent) {
        log.info("Sending Pay Results... = {}", payEvent);
        kafkaTemplate.send(payTopicName, payEvent);
    }
}
