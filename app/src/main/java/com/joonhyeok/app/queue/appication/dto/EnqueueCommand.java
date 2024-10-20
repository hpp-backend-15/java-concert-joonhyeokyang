package com.joonhyeok.app.queue.appication.dto;

public record EnqueueCommand(
        String userId
) {
    public static EnqueueCommand from(String userId) {
        return new EnqueueCommand(userId);
    }

}

