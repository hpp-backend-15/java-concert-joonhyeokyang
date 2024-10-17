package com.joonhyeok.app.queue.infra;

import com.joonhyeok.app.queue.domain.Queue;
import com.joonhyeok.app.queue.domain.QueueRepository;
import com.joonhyeok.app.queue.domain.QueueStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

import static com.joonhyeok.app.queue.domain.QueueStatus.ACTIVE;
import static org.apache.commons.lang3.math.NumberUtils.max;

public class MemoryQueueRepository implements QueueRepository {
    private HashMap<Long, Queue> map = new HashMap<>();
    private Long id = 0L;

    @Override
    public Optional<Long> findMaxPositionOfActivated() {
        Long lastActivateIdx = 0L;

        boolean hasActivated = false;
        if (map.isEmpty()) return Optional.empty();
        for (Long l : map.keySet()) {
            Queue queue = map.get(l);
            if (queue.getStatus() == ACTIVE) {
                hasActivated = true;
                lastActivateIdx = max(lastActivateIdx, queue.getId());
            }
        }
        if (hasActivated) {
            return Optional.of(lastActivateIdx);
        }
        else return Optional.empty();

    }

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
        Queue newQueue = new Queue(id, queue.getSessionId(), QueueStatus.WAIT, LocalDateTime.now(), null, null, null);
        map.put(newQueue.getId(), newQueue);
        id = id + 1;
        return newQueue;
    }

}
