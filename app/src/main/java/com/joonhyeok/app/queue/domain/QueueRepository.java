package com.joonhyeok.app.queue.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QueueRepository {

    Optional<Long> findMaxPositionOfActivated();

    Optional<Queue> findByUserId(String userId);

    Optional<Queue> findByWaitId(String wait);

    Optional<Queue> findById(Long id);

    Queue save(Queue queue);

    Long countByQueueStatus(QueueStatus queueStatus);

    List<Queue> findByQueueStatusAndLimit(QueueStatus queueStatus, Long limit);
    List<Queue> findByQueueStatusAndExpireAtAfter(QueueStatus queueStatus, LocalDateTime expireAt);
}
