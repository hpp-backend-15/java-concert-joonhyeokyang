package com.joonhyeok.app.queue.domain;

import java.util.Optional;

public interface QueueRepository {

    Optional<Long> findMaxPositionOfActivated();
    Optional<Queue> findByUserId(String userId);
    Optional<Queue> findByWaitId(String wait);
    Optional<Queue> findById(Long id);

    Queue save(Queue queue);

}
