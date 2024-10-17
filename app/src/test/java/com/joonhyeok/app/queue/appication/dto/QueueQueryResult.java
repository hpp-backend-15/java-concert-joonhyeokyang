package com.joonhyeok.app.queue.appication.dto;

import com.joonhyeok.app.queue.domain.Queue;

public record QueueQueryResult(
        String sessionId,
        Long position
) {

    public static QueueQueryResult of(Queue queue, Long lastActivatedId) {
        return new QueueQueryResult(queue.getSessionId(), queue.getPosition(lastActivatedId));
    }

}
