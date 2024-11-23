package com.joonhyeok.app.user.application.dto.outbox;

public record OutboxFindCommand(
        String type,
        Long relationalId
) {
}
