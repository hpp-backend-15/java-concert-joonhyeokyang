package com.joonhyeok.app.queue.infra;

import com.joonhyeok.app.queue.domain.Queue;
import com.joonhyeok.app.queue.domain.QueuePolicy;
import com.joonhyeok.app.queue.domain.QueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.joonhyeok.app.queue.domain.QueueStatus.ACTIVE;
import static com.joonhyeok.app.queue.domain.QueueStatus.WAIT;
import static java.lang.Math.max;

@Service
@RequiredArgsConstructor
public class QueueFixedTotalPolicy implements QueuePolicy {
    private final Long FIXED_TOTAL = 5L;
    private final Long ACTIVE_WILL_EXPIRE_IN_MINUTES = 10L;
    private final QueueRepository queueRepository;

    @Transactional
    @Override
    public void activate() {
        Long activateCount = queueRepository.countByStatus(ACTIVE);
        int limit = (int) max(0L, FIXED_TOTAL - activateCount);
        List<Queue> candidates = queueRepository.findByStatus(WAIT, Limit.of(limit));
        candidates.forEach(queue -> queue.activate(LocalDateTime.now(), LocalDateTime.now().plusMinutes(ACTIVE_WILL_EXPIRE_IN_MINUTES)));
    }

    @Transactional
    @Override
    public void expire() {
        List<Queue> candidates = queueRepository.findByStatusAndExpireAtAfter(ACTIVE, LocalDateTime.now());
        candidates.forEach(Queue::expire);
    }
}
