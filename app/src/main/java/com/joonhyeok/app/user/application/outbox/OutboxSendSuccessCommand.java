package com.joonhyeok.app.user.application.outbox;

public record OutboxSendSuccessCommand(
        String type,
        Long relationalId
) {
}
