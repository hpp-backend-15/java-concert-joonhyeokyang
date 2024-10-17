package com.joonhyeok.app.queue.appication;

import com.joonhyeok.app.queue.domain.Queue;
import com.joonhyeok.app.queue.domain.QueueRepository;
import com.joonhyeok.app.queue.appication.dto.EnqueueCommand;
import com.joonhyeok.app.queue.appication.dto.EnqueueResult;
import com.joonhyeok.app.queue.appication.dto.QueueQuery;
import com.joonhyeok.app.queue.appication.dto.QueueQueryResult;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.joonhyeok.app.queue.domain.QueueStatus.EXPIRED;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueueService {
    private final QueueRepository queueRepository;

    @Transactional
    public EnqueueResult enqueue(EnqueueCommand command) {
        String sessionId = command.sessionId();

        queueRepository.findBySessionId(sessionId).ifPresent(queue -> {
            throw new EntityExistsException("이미 대기중인 세션입니다. sessionId = " + queue.getSessionId());
        });

        Queue queue = Queue.create(sessionId);
        Queue savedQueue = queueRepository.save(queue);
        return new EnqueueResult(savedQueue.getId());
    }

    /**
     * 정책
     * WAIT -> 정상 반환
     * ACTIVATE -> 정상 반환 (앞선 대기줄 0명, 0초 대기)
     * EXPIRE -> 예외 반환
     * @param query
     * @return
     */
    public QueueQueryResult query(QueueQuery query) {
        String sessionId = query.sessionId();
        Queue queue = queueRepository.findBySessionId(sessionId).orElseThrow(() ->
                new EntityNotFoundException("존재하지 않는 대기자입니다. sessionId = " + sessionId));

        if (queue.getStatus() == EXPIRED) {
            throw  new IllegalStateException("이미 만료된 대기자입니다. sessionId = " + sessionId);
        }

        Long lastActivatedIdx = queueRepository.findMaxPositionOfActivated().orElse(-1L);

        return QueueQueryResult.of(queue, lastActivatedIdx);
    }
}