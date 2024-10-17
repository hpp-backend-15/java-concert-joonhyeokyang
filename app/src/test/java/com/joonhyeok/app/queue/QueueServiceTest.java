package com.joonhyeok.app.queue;

import com.joonhyeok.app.queue.appication.QueueService;
import com.joonhyeok.app.queue.domain.Queue;
import com.joonhyeok.app.queue.domain.QueueRepository;
import com.joonhyeok.app.queue.appication.dto.EnqueueCommand;
import com.joonhyeok.app.queue.appication.dto.EnqueueResult;
import com.joonhyeok.app.queue.appication.dto.QueueQuery;
import com.joonhyeok.app.queue.appication.dto.QueueQueryResult;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.joonhyeok.app.queue.domain.QueueStatus.WAIT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class QueueServiceTest {

    QueueService queueService;
    QueueRepository queueRepository;

    @BeforeEach
    void setUp() {
        queueRepository = new QueueMemoryRepository();
        queueService = new QueueService(queueRepository);
    }

    @Test
    void Queue_대기열_등록하는_경우_성공한다() throws Exception {
        //given

        //when
        EnqueueResult result = queueService.enqueue(EnqueueCommand.from("sessionId"));

        //then1
        Optional<Queue> optionalQueue = queueRepository.findById(result.id());
        assertThat(optionalQueue.isPresent()).isTrue();

        //then2
        Queue registeredQueue = optionalQueue.get();
        assertThat(registeredQueue.getId()).isEqualTo(0L);
        assertThat(registeredQueue.getStatus()).isEqualTo(WAIT);
    }

    @Test
    void Queue_이미대기하는_대기요청은_실패한다() throws Exception {
        //given
        queueService.enqueue(EnqueueCommand.from("sessionId1"));

        //when

        //then
        assertThatThrownBy(() -> queueService.enqueue(EnqueueCommand.from("sessionId1")))
                .isInstanceOf(EntityExistsException.class);
    }

    @Test
    void Queue_이미대기하는_요청을_쿼리하면_찾을수있어야한다() throws Exception {
        //given
        queueService.enqueue(EnqueueCommand.from("sessionId"));

        //when
        QueueQueryResult result = queueService.query(QueueQuery.from("sessionId"));

        //then
        assertThat(result.sessionId()).isEqualTo("sessionId");
    }

    @Test
    void Queue_대기하지않는다면_쿼리가_실패해야한다() throws Exception {
        //given
        queueService.enqueue(EnqueueCommand.from("sessionId"));

        //when
        //then
        assertThatThrownBy(() -> queueService.query(QueueQuery.from("differentSessionId")))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void Queue_만료된큐라면_쿼리가_실패해야한다() {
        //given
        queueService.enqueue(EnqueueCommand.from("sessionId"));
        Queue queue = queueRepository.findBySessionId("sessionId").orElseThrow();
        queue.activate(LocalDateTime.now().minusDays(1));
        queue.expire();

        //when
        //then
        assertThatThrownBy(() -> queueService.query(QueueQuery.from("sessionId")))
                .isInstanceOf(IllegalStateException.class);

    }

    @Test
    void 앞에10명이대기하고있고_새로활성화되려면_내순번은11번째다() throws Exception {
        //given
        for (int i = 0; i < 10; i++) {
            queueService.enqueue(EnqueueCommand.from("sessionId" + i));
        }
        queueService.enqueue(EnqueueCommand.from("sessionId"));

        //when
        QueueQueryResult myTurn = queueService.query(QueueQuery.from("sessionId"));

        //then
        assertThat(myTurn.sessionId()).isEqualTo("sessionId");
        assertThat(myTurn.position()).isEqualTo(11);
    }

    @Test
    void 앞에10명이활성중이고_새로활성화되려면_내순번은1번째다() throws Exception {
        //given
        for (int i = 0; i < 10; i++) {
            queueService.enqueue(EnqueueCommand.from("sessionId" + i));
            Queue queue = queueRepository.findBySessionId("sessionId"+i).get();
            queue.activate(LocalDateTime.now());
        }
        queueService.enqueue(EnqueueCommand.from("sessionId"));

        //when
        QueueQueryResult myTurn = queueService.query(QueueQuery.from("sessionId"));

        //then
        assertThat(myTurn.sessionId()).isEqualTo("sessionId");
        assertThat(myTurn.position()).isEqualTo(1);

    }

//    @Test
//    void 앞에10명이대기하고있고_새로활성화되려면_최대20초는기다려야하고_내순번은11번째다() throws Exception {
//        //given
//        for (int i = 0; i < 10; i++) {
//            queueService.enqueue(EnqueueCommand.from("sessionId" + i));
//        }
//        queueService.enqueue(EnqueueCommand.from("sessionId"));
//
//        //when
//        QueueQueryResult myTurn = queueService.query(QueueQuery.from("sessionId"));
//
//        //then
//        assertThat(myTurn.sessionId()).isEqualTo("sessionId");
//        assertThat(myTurn.position()).isEqualTo(10);
//        assertThat(myTurn.estimatedWaitTime()).isAfterOrEqualTo(LocalDateTime.now().plusSeconds(2 * Constants.SCHEDULER_ACTIVATE_PERIOD_IN_SECONDS - 1));
//
//    }
//
//    @Test
//    void 앞에10명이활성중이고_새로활성화되려면_최대10초는기다려야하고_내순번은1번째다() throws Exception {
//        //given
//        for (int i = 0; i < 10; i++) {
//            queueService.enqueue(EnqueueCommand.from("sessionId" + i));
//            Queue queue = queueRepository.findBySessionId("sessionId"+i).get();
//            queue.activate(LocalDateTime.now());
//        }
//        queueService.enqueue(EnqueueCommand.from("sessionId"));
//
//        //when
//        QueueQueryResult myTurn = queueService.query(QueueQuery.from("sessionId"));
//
//        //then
//        assertThat(myTurn.sessionId()).isEqualTo("sessionId");
//        assertThat(myTurn.position()).isEqualTo(1);
//        assertThat(myTurn.estimatedWaitTime()).isAfterOrEqualTo(LocalDateTime.now().plusSeconds(1 * Constants.SCHEDULER_ACTIVATE_PERIOD_IN_SECONDS - 1));
//
//    }

}
