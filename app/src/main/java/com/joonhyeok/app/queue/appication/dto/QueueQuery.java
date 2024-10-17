package com.joonhyeok.app.queue.appication.dto;

public record QueueQuery(
        String sessionId
) {
    public static QueueQuery from(String sessionId) {
        return new QueueQuery(sessionId);
    }
}
