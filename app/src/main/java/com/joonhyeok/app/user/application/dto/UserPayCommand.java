package com.joonhyeok.app.user.application.dto;

public record UserPayCommand(
        Long userId,
        Long reservationId
) {
}
