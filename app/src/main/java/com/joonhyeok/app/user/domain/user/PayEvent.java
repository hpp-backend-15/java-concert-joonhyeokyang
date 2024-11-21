package com.joonhyeok.app.user.domain.user;

import com.joonhyeok.app.user.application.dto.user.UserPayResult;

public record PayEvent(
        UserPayResult result
) {
}
