package com.joonhyeok.app.user.domain;

import com.joonhyeok.app.user.application.ExternalPayInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayEventListener {
    private final ExternalPayInfoService payInfoService;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void sendPayInfo(PayEvent payEvent) {
        try {
            payInfoService.sendPayInfo(payEvent.result());
        } catch (Exception e) {
            log.info("주문정보 전달에 실패했어요~잉");
        }
    }
}
