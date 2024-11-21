package com.joonhyeok.app.user.application.outbox;

public record OutboxSendFailCommand(
        String type,
        Long relationalId
) {
}
