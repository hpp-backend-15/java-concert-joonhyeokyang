package com.joonhyeok.app.queue.domain;

import java.util.Optional;

public interface QueueRepository {
    Optional<Queue> findBySessionId(String sessionId);
    Optional<Queue> findById(Long id);

    Queue save(Queue queue);

}
