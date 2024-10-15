package com.joonhyeok.app.queue.domain.service;

import com.joonhyeok.app.queue.domain.Queue;
import com.joonhyeok.app.queue.domain.QueueRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
