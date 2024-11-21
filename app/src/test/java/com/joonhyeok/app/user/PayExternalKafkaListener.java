package com.joonhyeok.app.user;

import com.joonhyeok.app.user.domain.pay.PayEvent;

import java.util.concurrent.CountDownLatch;

/**
 * 외부 API 보낸 데이터를 받는지 테스트하기 위하여 생성한 Listener
 */
//@Service
public class PayExternalKafkaListener {

    private final CountDownLatch latch = new CountDownLatch(1);

    private PayEvent received;

//    @KafkaListener(topics = "pay-event-step17", containerFactory = "payKafkaListenerContainerFactory")
    public void listen(PayEvent payEvent) {
        received = payEvent;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public PayEvent getReceived() {
        return received;
    }
}
