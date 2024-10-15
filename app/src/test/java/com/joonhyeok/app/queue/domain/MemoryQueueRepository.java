package com.joonhyeok.app.queue.domain;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

public class MemoryQueueRepository implements QueueRepository {
    private HashMap<Long, Queue> map = new HashMap<>();
    private Long id = 0L;

    @Override
    public Optional<Queue> findBySessionId(String sessionId) {
        for (Queue value : map.values()) {
            if (value.getSessionId().equals(sessionId))
                return Optional.of(value);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Queue> findById(Long id) {
        return Optional.ofNullable(map.get(id));
    }

    @Override
    public Queue save(Queue queue) {
        id = id + 1;
        Queue newQueue = new Queue(id, queue.getSessionId(), QueueStatus.WAIT, LocalDateTime.now(), null, null, null);
        map.put(newQueue.getId(), newQueue);
        return newQueue;
    }

}
