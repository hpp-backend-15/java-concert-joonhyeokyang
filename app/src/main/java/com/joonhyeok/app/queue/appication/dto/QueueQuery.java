package com.joonhyeok.app.queue.appication.dto;

public record QueueQuery(
        String waitId
) {
    public static QueueQuery from(String waitId) {
        return new QueueQuery(waitId);
    }
}
