package com.joonhyeok.app.queue.appication.dto;

import com.joonhyeok.app.queue.domain.Queue;

public record QueueQueryResult(
        String waitId,
        Long position
) {

    public static QueueQueryResult of(Queue queue, Long lastActivatedId) {
        return new QueueQueryResult(queue.getWaitId(), queue.getPosition(lastActivatedId));
    }

}