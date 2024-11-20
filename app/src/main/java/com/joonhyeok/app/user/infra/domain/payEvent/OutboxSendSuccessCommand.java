package com.joonhyeok.app.user.infra.domain.payEvent;

public record OutboxSendSuccessCommand(
        String type,
        Long relationalId
) {
}
