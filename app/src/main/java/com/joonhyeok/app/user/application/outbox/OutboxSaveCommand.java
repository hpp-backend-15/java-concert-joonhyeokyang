package com.joonhyeok.app.user.application.outbox;

import com.joonhyeok.app.user.domain.outbox.Outbox;

public record OutboxSaveCommand(
        String type,
        Long relationalId
) {
    public Outbox createOutbox() {
        return Outbox.issueOutbox(type, relationalId);
    }
}
