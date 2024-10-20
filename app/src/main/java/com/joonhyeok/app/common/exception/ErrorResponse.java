package com.joonhyeok.app.common.exception;

public record ErrorResponse(
        int code,
        String message
) {
}
