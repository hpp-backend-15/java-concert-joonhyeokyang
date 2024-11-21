package com.joonhyeok.app.user.domain.user;

import com.joonhyeok.app.user.application.dto.user.UserPayResult;
import com.joonhyeok.app.user.domain.outbox.Outbox;

public record PayEvent(
        UserPayResult result
) {
    public static PayEvent from(Outbox outbox) {
        return new PayEvent(new UserPayResult(outbox.getRelationalId()));
    }
}
