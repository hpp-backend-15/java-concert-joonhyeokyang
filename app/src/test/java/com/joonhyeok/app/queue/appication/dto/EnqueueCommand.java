package com.joonhyeok.app.queue.appication.dto;

public record EnqueueCommand(
        String sessionId
) {
    public static EnqueueCommand from(String sessionId) {
        return new EnqueueCommand(sessionId);
    }

}

