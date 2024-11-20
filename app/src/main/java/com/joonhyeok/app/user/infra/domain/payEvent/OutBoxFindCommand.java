package com.joonhyeok.app.user.infra.domain.payEvent;

public record OutBoxFindCommand(
        String type,
        Long relationalId
) {
}
