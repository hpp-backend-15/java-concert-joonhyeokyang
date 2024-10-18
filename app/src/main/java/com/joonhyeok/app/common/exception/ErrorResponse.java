package com.joonhyeok.app.common.exception;

public record ErrorResponse(
        String code,
        String message
) {
}
