package com.joonhyeok.app.reservation.application.dto;

public record MakeReservationCommand(
        Long seatId,
        Long userId
) {
}
