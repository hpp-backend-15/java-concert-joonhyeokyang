package com.joonhyeok.app.user.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * 외부 API 보낸 데이터를 받는지 테스트하기 위하여 생성한 Listener
 */
@Slf4j
@Service
public class PayExternalKafkaListener {
    @KafkaListener(topics = "#{'${spring.kafka.topic.names}'}", containerFactory = "PayEventListenerContainerFactory")
    public void listen(PayEvent payEvent) {
        log.info("consumed payEvent:{}", payEvent);
    }
}
