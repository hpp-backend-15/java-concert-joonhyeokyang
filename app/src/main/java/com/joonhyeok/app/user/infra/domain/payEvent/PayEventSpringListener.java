package com.joonhyeok.app.user.infra.domain.payEvent;

import com.joonhyeok.app.user.application.PayInfoService;
import com.joonhyeok.app.user.domain.PayEvent;
import com.joonhyeok.app.user.domain.PayEventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Service
@Slf4j
@RequiredArgsConstructor
public class PayEventSpringListener implements PayEventListener {
    private final PayInfoService payInfoService;

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void sendPayInfo(PayEvent payEvent) {
        try {
            log.info("listen pay event");
            payInfoService.sendPayInfo(payEvent);
        } catch (Exception e) {
            log.info("주문정보 전달에 실패했어요~잉");
        }
    }
}
