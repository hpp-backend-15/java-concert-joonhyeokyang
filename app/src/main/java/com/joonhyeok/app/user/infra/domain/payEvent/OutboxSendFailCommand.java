package com.joonhyeok.app.user.infra.domain.payEvent;

public record OutboxSendFailCommand(
        String type,
        Long relationalId
) {
}
