package com.joonhyeok.app.user.application.outbox;

public record OutboxFindCommand(
        String type,
        Long relationalId
) {
}
