package com.joonhyeok.app.user.application.dto.user;

public record UserPayCommand(
        Long userId,
        Long reservationId
) {
}
