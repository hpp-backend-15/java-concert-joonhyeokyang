package com.joonhyeok.app.user.domain.pay;

import com.joonhyeok.app.user.application.dto.UserPayResult;

public record PayEvent(
        UserPayResult result
) {
}
