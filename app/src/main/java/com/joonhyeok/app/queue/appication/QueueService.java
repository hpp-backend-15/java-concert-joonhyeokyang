package com.joonhyeok.app.queue.appication;

import com.joonhyeok.app.queue.appication.dto.EnqueueCommand;
import com.joonhyeok.app.queue.appication.dto.EnqueueResult;
import com.joonhyeok.app.queue.appication.dto.QueueQuery;
import com.joonhyeok.app.queue.appication.dto.QueueQueryResult;
import com.joonhyeok.app.queue.domain.Queue;
import com.joonhyeok.app.queue.domain.QueueRepository;
import com.joonhyeok.app.user.domain.user.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueueService {
    private final QueueRepository queueRepository;
    private final UserRepository userRepository;

    @Transactional
    public EnqueueResult enqueue(EnqueueCommand command) {
        Long userId = command.userId();

        userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("가입된 유저가 아닙니다. 요청 Id = " + userId)
        );

        queueRepository.findByUserId(userId).ifPresent(queue -> {
            throw new EntityExistsException("이미 대기중인 대기자입니다. userId = " + queue.getWaitId());
        });

        Queue queue = Queue.create(userId);
        Queue savedQueue = queueRepository.save(queue);
        return new EnqueueResult(savedQueue.getId(), savedQueue.getWaitId());
    }

    /**
     * 정책
     * WAIT -> 정상 반환
     * ACTIVE -> 정상 반환 (앞선 대기줄 0명, 0초 대기)
     * EXPIRE -> 예외 반환
     *
     * @param query
     * @return
     */
    public QueueQueryResult query(QueueQuery query) {
        String waitId = query.waitId();

        Queue queue = queueRepository.findByWaitId(waitId).orElseThrow(() ->
                new EntityNotFoundException("존재하지 않는 대기자입니다. waitId = " + waitId));


        Long lastActivatedIdx = queueRepository.findMaxPositionOfActivated().orElse(0L);

        return QueueQueryResult.of(queue, lastActivatedIdx);
    }
}
