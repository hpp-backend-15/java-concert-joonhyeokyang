package com.joonhyeok.app.queue.domain;

import org.springframework.data.domain.Limit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QueueRepository {

    Optional<Long> findMaxPositionOfActivated();

    Optional<Queue> findByUserId(Long userId);

    Optional<Queue> findByWaitId(String wait);

    Optional<Queue> findById(Long id);

    Queue save(Queue queue);

    Long countByStatus(QueueStatus queueStatus);

    List<Queue> findByStatus(QueueStatus queueStatus, Limit limit);
    List<Queue> findByStatusAndExpireAtAfter(QueueStatus queueStatus, LocalDateTime expireAt);
}
