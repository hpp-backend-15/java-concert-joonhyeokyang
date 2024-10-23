package com.joonhyeok.app.queue.appication.dto;

import com.joonhyeok.app.queue.domain.Queue;
import com.joonhyeok.app.queue.domain.QueueStatus;

public record QueueQueryResult(
        String waitId,
        Long userId,
        Long position,
        QueueStatus status
) {

    public static QueueQueryResult of(Queue queue, Long lastActivatedId) {
        return new QueueQueryResult(
                queue.getWaitId(),
                queue.getUserId(),
                queue.getPosition(lastActivatedId),
                queue.getStatus());
    }

}
