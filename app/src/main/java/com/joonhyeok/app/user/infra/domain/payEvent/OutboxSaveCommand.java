package com.joonhyeok.app.user.infra.domain.payEvent;

public record OutboxSaveCommand(
        String type,
        Long relationalId
) {
    public Outbox createOutbox() {
        return Outbox.issueOutbox(type, relationalId);
    }
}
