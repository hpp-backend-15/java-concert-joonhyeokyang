package com.joonhyeok.app.queue.infra;

import com.joonhyeok.app.queue.domain.Queue;
import com.joonhyeok.app.queue.domain.QueuePolicy;
import com.joonhyeok.app.queue.domain.QueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
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
    private final Long ACTIVATE_WILL_EXPIRE_IN_MINUTES = 10L;
    private final QueueRepository queueRepository;

    @Transactional
    @Override
    public void activate() {
        Long activateCount = queueRepository.countByQueueStatus(ACTIVE);
        Long limit = max(0L, FIXED_TOTAL - activateCount);
        List<Queue> candidates = queueRepository.findByQueueStatusAndLimit(WAIT, limit);
        candidates.forEach(queue -> queue.activate(LocalDateTime.now(), LocalDateTime.now().plusMinutes(ACTIVATE_WILL_EXPIRE_IN_MINUTES)));
    }

    @Transactional
    @Override
    public void expire() {
        List<Queue> candidates = queueRepository.findByQueueStatusAndExpireAtAfter(ACTIVE, LocalDateTime.now());
        candidates.forEach(Queue::expire);
    }
}
