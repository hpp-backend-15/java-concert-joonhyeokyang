package com.joonhyeok.app.user.infra.domain.user;

import com.joonhyeok.app.user.application.OutboxService;
import com.joonhyeok.app.user.application.PayInfoService;
import com.joonhyeok.app.user.application.dto.outbox.OutboxSaveCommand;
import com.joonhyeok.app.user.application.dto.outbox.OutboxSendFailCommand;
import com.joonhyeok.app.user.domain.user.PayEvent;
import com.joonhyeok.app.user.domain.user.PayEventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;
import static org.springframework.transaction.event.TransactionPhase.BEFORE_COMMIT;

@Service
@Slf4j
@RequiredArgsConstructor
public class PayEventSpringListener implements PayEventListener {
    private final PayInfoService payInfoService;
    private final OutboxService outBoxService;

    @TransactionalEventListener(phase = BEFORE_COMMIT)
    public void outBoxing(PayEvent payEvent) {
        log.info("outBoxing >>> {}", payEvent);
        OutboxSaveCommand command = new OutboxSaveCommand("pay", payEvent.result().reservationId());
        outBoxService.save(command);
    }

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void sendPayInfo(PayEvent payEvent) {
        try {
            log.info("listen pay event");
            payInfoService.sendPayInfo(payEvent);
        } catch (Exception e) {
            log.info("주문정보 전달에 실패했어요~잉");
            outBoxService.updateFailStatus(new OutboxSendFailCommand("pay", payEvent.result().reservationId()));
        }
    }
}
