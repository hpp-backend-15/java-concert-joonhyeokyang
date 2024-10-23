package com.joonhyeok.app.user.application.dto;

import java.time.LocalDateTime;

public record AccountChargeResult(
        Long balance,
        LocalDateTime chargedAt
) {
}
