package com.joonhyeok.app.queue.appication;

import com.joonhyeok.app.queue.domain.Queue;
import com.joonhyeok.app.queue.domain.QueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.joonhyeok.app.queue.domain.QueueStatus.ACTIVE;
import static com.joonhyeok.app.queue.domain.QueueStatus.WAIT;

@Service
@RequiredArgsConstructor
public class QueueFixedTotalPolicy {
    private final Long FIXED_TOTAL = 500L;
    private final Long ACTIVE_WILL_EXPIRE_IN_MINUTES = 10L;
    private final QueueRepository queueRepository;

    @Transactional
    public void activate() {
        int limit = Math.toIntExact(FIXED_TOTAL);
        List<Queue> candidates = queueRepository.findByStatus(WAIT, Limit.of(limit));
        candidates.forEach(queue -> queue.activate(LocalDateTime.now(), LocalDateTime.now().plusMinutes(ACTIVE_WILL_EXPIRE_IN_MINUTES)));
    }

    @Transactional
    public void expire() {
        List<Queue> candidates = queueRepository.findByStatusAndExpireAtAfter(ACTIVE, LocalDateTime.now());
        candidates.forEach(Queue::expire);
    }
}
