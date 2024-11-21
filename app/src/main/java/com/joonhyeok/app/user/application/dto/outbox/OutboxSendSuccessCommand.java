package com.joonhyeok.app.user.application.dto.outbox;

public record OutboxSendSuccessCommand(
        String type,
        Long relationalId
) {
}
