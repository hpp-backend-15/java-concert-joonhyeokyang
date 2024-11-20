package com.joonhyeok.app.user.infra.domain.payEvent;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutBoxService {
    private final OutBoxRepository outBoxRepository;

    @Transactional
    public OutBox save(OutBoxSaveCommand command) {
        OutBox outBox = command.createOutBox();
        OutBox save = outBoxRepository.save(outBox);
        return save;
    }

    @Transactional
    public OutBox findOutBoxById(OutBoxFindCommand command) {
        String type = command.type();
        Long relationalId = command.relationalId();
        return outBoxRepository.findOutBoxByTypeAndRelationalId(type, relationalId)
                .orElseThrow(() -> new EntityNotFoundException("해당 outBox가 존재하지 않습니다. type: " + type + "relationalId: " + relationalId));
    }

    @Transactional
    public void updateFailStatus(OutBoxSendFailCommand command) {
        String type = command.type();
        Long relationalId = command.relationalId();
        OutBox outBox = outBoxRepository.findOutBoxByTypeAndRelationalId(type, relationalId)
                .orElseThrow(() -> new EntityNotFoundException("해당 outBox가 존재하지 않습니다. type: " + type + "relationalId: " + relationalId));
        outBox.changeStatusToFail();
    }
}
