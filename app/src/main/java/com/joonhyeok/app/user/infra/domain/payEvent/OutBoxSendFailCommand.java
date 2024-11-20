package com.joonhyeok.app.user.infra.domain.payEvent;

public record OutBoxSendFailCommand(
        String type,
        Long relationalId
) {
}
