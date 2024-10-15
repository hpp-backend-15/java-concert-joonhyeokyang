package com.joonhyeok.app.queue.domain.service;

import com.joonhyeok.app.queue.domain.MemoryQueueRepository;
import com.joonhyeok.app.queue.domain.Queue;
import com.joonhyeok.app.queue.domain.QueueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.joonhyeok.app.queue.domain.QueueStatus.WAIT;
import static org.assertj.core.api.Assertions.assertThat;

public class QueueServiceTest {

    QueueService queueService;
    QueueRepository queueRepository;

    @BeforeEach
    void setUp() {
        queueRepository = new MemoryQueueRepository();
        queueService = new QueueService(queueRepository);
    }

    @Test
    void Queue_대기열_등록하는_경우_성공한다() throws Exception {
        //given

        //when
        EnqueueResult result = queueService.enqueue(EnqueueCommand.from("sessionId"));

        //then
        Optional<Queue> optionalQueue = queueRepository.findById(result.id());
        assertThat(optionalQueue.isPresent()).isTrue();
        Queue registeredQueue = optionalQueue.get();
        assertThat(registeredQueue.getId()).isEqualTo(1L);
        assertThat(registeredQueue.getStatus()).isEqualTo(WAIT);

    }
}
