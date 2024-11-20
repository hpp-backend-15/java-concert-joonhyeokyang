package com.joonhyeok.app.user.infra.domain.payEvent;

public record OutboxFindCommand(
        String type,
        Long relationalId
) {
}
