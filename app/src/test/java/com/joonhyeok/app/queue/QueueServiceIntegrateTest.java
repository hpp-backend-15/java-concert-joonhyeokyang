package com.joonhyeok.app.queue;

import com.joonhyeok.app.queue.appication.QueueService;
import com.joonhyeok.app.queue.appication.dto.EnqueueCommand;
import com.joonhyeok.app.queue.appication.dto.EnqueueResult;
import com.joonhyeok.app.queue.appication.dto.QueueQuery;
import com.joonhyeok.app.queue.appication.dto.QueueQueryResult;
import com.joonhyeok.app.queue.domain.Queue;
import com.joonhyeok.app.queue.domain.QueueRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.joonhyeok.app.queue.domain.QueueStatus.WAIT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yaml")
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class QueueServiceIntegrateTest {

    @Autowired
    QueueService queueService;

    @Autowired
    QueueRepository queueRepository;

    @Test
    void Queue_대기열_등록하는_경우_성공한다() throws Exception {
        //given

        //when
        EnqueueResult result = queueService.enqueue(EnqueueCommand.from("userId"));

        //then1
        Optional<Queue> optionalQueue = queueRepository.findById(result.id());
        assertThat(optionalQueue.isPresent()).isTrue();

        //then2
        Queue registeredQueue = optionalQueue.get();
        assertThat(registeredQueue.getId()).isEqualTo(1L);
        assertThat(registeredQueue.getStatus()).isEqualTo(WAIT);
    }

    @Test
    void Queue_이미대기하는_대기요청은_실패한다() throws Exception {
        //given
        queueService.enqueue(EnqueueCommand.from("waitId1"));

        //when

        //then
        assertThatThrownBy(() -> queueService.enqueue(EnqueueCommand.from("waitId1")))
                .isInstanceOf(EntityExistsException.class);
    }

    @Test
    void Queue_이미대기하는_요청을_쿼리하면_찾을수있어야한다() throws Exception {
        //given
        Long queueId = queueService.enqueue(EnqueueCommand.from("userId")).id();

        //when
        String waitId = queueRepository.findById(queueId).orElseThrow().getWaitId();

        QueueQueryResult result = queueService.query(QueueQuery.from(waitId));

        //then
        assertThat(result.waitId()).isEqualTo(waitId);
    }

    @Test
    void Queue_대기하지않는다면_쿼리가_실패해야한다() throws Exception {
        //given
        queueService.enqueue(EnqueueCommand.from("userId"));

        //when
        //then
        assertThatThrownBy(() -> queueService.query(QueueQuery.from("differentWaitId")))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void Queue_만료된큐라면_쿼리가_실패해야한다() {
        //given
        queueService.enqueue(EnqueueCommand.from("userId"));
        Queue queue = queueRepository.findByUserId("userId").orElseThrow();
        queue.activate(LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(23));
        queue.expire();
        queueRepository.save(queue);

        //when
        //then
        assertThatThrownBy(() -> queueService.query(QueueQuery.from(queue.getWaitId())))
                .isInstanceOf(IllegalStateException.class);

    }

    @Test
    void 앞에10명이대기하고있고_새로활성화되려면_내순번은11번째다() throws Exception {
        //given
        for (int i = 0; i < 10; i++) {
            queueService.enqueue(EnqueueCommand.from("userId" + i));
        }
        Long queueId = queueService.enqueue(EnqueueCommand.from("userId")).id();

        //when
        String waitId = queueRepository.findById(queueId).orElseThrow().getWaitId();
        QueueQueryResult myTurn = queueService.query(QueueQuery.from(waitId));

        //then
        assertThat(myTurn.position()).isEqualTo(11);
    }

    @Test
    void 앞에10명이활성중이고_새로활성화되려면_내순번은1번째다() throws Exception {
        //given
        for (int i = 0; i < 10; i++) {
            queueService.enqueue(EnqueueCommand.from("userId" + i));
            Queue queue = queueRepository.findByUserId("userId" + i).get();
            queue.activate(LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));
            queueRepository.save(queue);
        }

        Long queueId = queueService.enqueue(EnqueueCommand.from("userId")).id();
        //when
        String waitId = queueRepository.findById(queueId).orElseThrow().getWaitId();
        QueueQueryResult myTurn = queueService.query(QueueQuery.from(waitId));

        //then
        assertThat(myTurn.position()).isEqualTo(1);

    }

//    @Test
//    void 앞에10명이대기하고있고_새로활성화되려면_최대20초는기다려야하고_내순번은11번째다() throws Exception {
//        //given
//        for (int i = 0; i < 10; i++) {
//            queueService.enqueue(EnqueueCommand.from("userId" + i));
//        }
//        queueService.enqueue(EnqueueCommand.from("userId"));
//
//        //when
//        QueueQueryResult myTurn = queueService.query(QueueQuery.from("userId"));
//
//        //then
//        assertThat(myTurn.userId()).isEqualTo("userId");
//        assertThat(myTurn.position()).isEqualTo(10);
//        assertThat(myTurn.estimatedWaitTime()).isAfterOrEqualTo(LocalDateTime.now().plusSeconds(2 * Constants.SCHEDULER_ACTIVE_PERIOD_IN_SECONDS - 1));
//
//    }
//
//    @Test
//    void 앞에10명이활성중이고_새로활성화되려면_최대10초는기다려야하고_내순번은1번째다() throws Exception {
//        //given
//        for (int i = 0; i < 10; i++) {
//            queueService.enqueue(EnqueueCommand.from("userId" + i));
//            Queue queue = queueRepository.findByWaitId("userId"+i).get();
//            queue.activate(LocalDateTime.now());
//        }
//        queueService.enqueue(EnqueueCommand.from("userId"));
//
//        //when
//        QueueQueryResult myTurn = queueService.query(QueueQuery.from("userId"));
//
//        //then
//        assertThat(myTurn.userId()).isEqualTo("userId");
//        assertThat(myTurn.position()).isEqualTo(1);
//        assertThat(myTurn.estimatedWaitTime()).isAfterOrEqualTo(LocalDateTime.now().plusSeconds(1 * Constants.SCHEDULER_ACTIVE_PERIOD_IN_SECONDS - 1));
//
//    }

}
