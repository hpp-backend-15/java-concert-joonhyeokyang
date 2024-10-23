package com.joonhyeok.app.queue.appication.dto;

public record EnqueueResult(
        Long userId,
        String waitId
) {
}
