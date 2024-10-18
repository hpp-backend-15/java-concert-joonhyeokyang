package com.joonhyeok.app.queue.domain;

import java.util.Optional;

public interface QueueRepository {

    Optional<Long> findMaxPositionOfActivated();
    Optional<Queue> findByWaitId(String waitId);
    Optional<Queue> findById(Long id);

    Queue save(Queue queue);

}
