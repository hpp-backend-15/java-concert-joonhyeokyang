package com.joonhyeok.app.user.application.dto.outbox;

public record OutboxSendFailCommand(
        String type,
        Long relationalId
) {
}
