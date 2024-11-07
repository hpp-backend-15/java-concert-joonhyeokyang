package com.joonhyeok.app.reservation.application.dto;

public record MakeReservationCommand(
        Long concertId,
        Long performanceDateId,
        Long seatId,
        Long userId
) {
}
