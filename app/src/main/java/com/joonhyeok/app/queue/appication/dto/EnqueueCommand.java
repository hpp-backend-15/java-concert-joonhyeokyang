package com.joonhyeok.app.queue.appication.dto;

public record EnqueueCommand(
        Long userId
) {
    public static EnqueueCommand from(Long userId) {
        return new EnqueueCommand(userId);
    }

}

