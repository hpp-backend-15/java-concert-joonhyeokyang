package com.joonhyeok.app.queue;

import com.joonhyeok.app.queue.domain.Queue;
import com.joonhyeok.app.queue.domain.QueueRepository;
import com.joonhyeok.app.queue.domain.QueueStatus;
import org.springframework.data.domain.Limit;

import java.time.LocalDateTime;
import java.util.*;

import static com.joonhyeok.app.queue.domain.QueueStatus.ACTIVE;
import static org.apache.commons.lang3.math.NumberUtils.max;

public class QueueMemoryRepository implements QueueRepository {
    private HashMap<Long, Queue> map = new HashMap<>();
    private Long id = 1L;

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
        } else return Optional.empty();

    }

    @Override
    public Optional<Queue> findByWaitId(String waitId) {
        for (Queue value : map.values()) {
            if (value.getWaitId().equals(waitId))
                return Optional.of(value);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Queue> findByUserId(String userId) {
        for (Queue value : map.values()) {
            if (value.getUserId().equals(userId))
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
        Queue newQueue = new Queue(id, queue.getWaitId(), queue.getUserId(), QueueStatus.WAIT, LocalDateTime.now(), null, null, null);
        map.put(newQueue.getId(), newQueue);
        id = id + 1;
        return newQueue;
    }

    @Override
    public Long countByStatus(QueueStatus queueStatus) {
        Long count = 0L;
        for (Queue value : map.values()) {
            if (value.getStatus() == queueStatus) {
                count++;
            }
        }
        return count;
    }

    @Override
    public List<Queue> findByStatus(QueueStatus queueStatus, Limit limit) {
        List<Queue> candidates = new ArrayList<>();
        for (Queue value : map.values()) {
            if (value.getStatus() == queueStatus) {
                candidates.add(value);
            }
        }
        candidates.sort(Comparator.comparingLong(Queue::getId));

        return candidates.subList(0, limit.max());
    }

    @Override
    public List<Queue> findByStatusAndExpireAtAfter(QueueStatus queueStatus, LocalDateTime expireAt) {
        List<Queue> candidates = new ArrayList<>();
        for (Queue value : map.values()) {
            if (value.getStatus() == queueStatus && value.getExpireAt().isAfter(expireAt)) {
                candidates.add(value);
            }
        }
        candidates.sort(Comparator.comparingLong(Queue::getId));

        return candidates;
    }
}
