package com.joonhyeok.app.queue.domain.service;

import com.joonhyeok.app.queue.domain.Queue;

public record EnqueueCommand(
        String sessionId
) {
    public static EnqueueCommand from(String sessionId) {
        return new EnqueueCommand(sessionId);
    }

}

