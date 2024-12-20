package com.joonhyeok.app.queue.appication.dto;

import com.joonhyeok.app.queue.domain.Queue;

import java.time.LocalDateTime;

public record QueueQueryETAResult(
        String waitId,
        Long position,
        LocalDateTime estimatedWaitTime
) {

    public static QueueQueryETAResult of(Queue queue, Long lastActivatedId) {
        return new QueueQueryETAResult(
                queue.getWaitId(),
                queue.getPosition(lastActivatedId),
                queue.getEstimateWaitTime(lastActivatedId));
    }

}
