package com.joonhyeok.app.user.application;

import com.joonhyeok.app.user.application.dto.outbox.OutboxFindCommand;
import com.joonhyeok.app.user.application.dto.outbox.OutboxSaveCommand;
import com.joonhyeok.app.user.application.dto.outbox.OutboxSendFailCommand;
import com.joonhyeok.app.user.application.dto.outbox.OutboxSendSuccessCommand;
import com.joonhyeok.app.user.domain.outbox.Outbox;
import com.joonhyeok.app.user.domain.outbox.OutboxRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutboxService {
    private final OutboxRepository outBoxRepository;

    @Transactional
    public Outbox save(OutboxSaveCommand command) {
        Outbox outBox = command.createOutbox();
        return outBoxRepository.save(outBox);
    }

    @Transactional
    public Outbox findOutboxById(OutboxFindCommand command) {
        String type = command.type();
        Long relationalId = command.relationalId();
        return outBoxRepository.findOutboxByTypeAndRelationalId(type, relationalId)
                .orElseThrow(() -> new EntityNotFoundException("해당 outBox가 존재하지 않습니다. type: " + type + "relationalId: " + relationalId));
    }

    @Transactional
    public void updateFailStatus(OutboxSendFailCommand command) {
        String type = command.type();
        Long relationalId = command.relationalId();
        Outbox outBox = outBoxRepository.findOutboxByTypeAndRelationalId(type, relationalId)
                .orElseThrow(() -> new EntityNotFoundException("해당 outBox가 존재하지 않습니다. type: " + type + "relationalId: " + relationalId));
        outBox.changeStatusToFail();
    }

    @Transactional
    public void updateSuccessStatus(OutboxSendSuccessCommand command) {
        String type = command.type();
        Long relationalId = command.relationalId();
        Outbox outBox = outBoxRepository.findOutboxByTypeAndRelationalId(type, relationalId)
                .orElseThrow(() -> new EntityNotFoundException("해당 outBox가 존재하지 않습니다. type: " + type + "relationalId: " + relationalId));
        outBox.changeStatusToSuccess();
    }
}
