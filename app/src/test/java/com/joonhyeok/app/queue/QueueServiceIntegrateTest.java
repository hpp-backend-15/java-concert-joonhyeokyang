package com.joonhyeok.app.queue;

import com.joonhyeok.app.queue.appication.QueueService;
import com.joonhyeok.app.queue.appication.dto.EnqueueCommand;
import com.joonhyeok.app.queue.appication.dto.EnqueueResult;
import com.joonhyeok.app.queue.appication.dto.QueueQuery;
import com.joonhyeok.app.queue.appication.dto.QueueQueryResult;
import com.joonhyeok.app.queue.domain.Queue;
import com.joonhyeok.app.queue.domain.QueueRepository;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.joonhyeok.app.queue.domain.QueueStatus.WAIT;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.BEFORE_EACH_TEST_METHOD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Sql("/ddl-test.sql")
@AutoConfigureEmbeddedDatabase(refresh = BEFORE_EACH_TEST_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class QueueServiceIntegrateTest {

    @Autowired
    QueueService queueService;

    @Autowired
    QueueRepository queueRepository;

    @Test
    void Queue_대기열_등록하는_경우_성공한다() {
        //given

        //when
        EnqueueResult result = queueService.enqueue(EnqueueCommand.from(1L));

        //then1
        Optional<Queue> optionalQueue = queueRepository.findById(result.userId());
        assertThat(optionalQueue).isPresent();

        //then2
        Queue registeredQueue = optionalQueue.get();
        assertThat(registeredQueue.getId()).isEqualTo(1L);
        assertThat(registeredQueue.getStatus()).isEqualTo(WAIT);
    }

    @Test
    void Queue_이미대기하는_대기요청은_실패한다() {
        //given
        queueService.enqueue(EnqueueCommand.from(1L));

        //when
        EnqueueCommand dupCommand = EnqueueCommand.from(1L);

        //then
        assertThatThrownBy(() -> queueService.enqueue(dupCommand))
                .isInstanceOf(EntityExistsException.class);
    }

    @Test
    void Queue_이미대기하는_요청을_쿼리하면_찾을수있어야한다() {
        //given
        Long queueId = queueService.enqueue(EnqueueCommand.from(1L)).userId();

        //when
        String waitId = queueRepository.findById(queueId).orElseThrow().getWaitId();

        QueueQueryResult result = queueService.query(QueueQuery.from(waitId));

        //then
        assertThat(result.waitId()).isEqualTo(waitId);
    }

    @Test
    void Queue_대기하지않는다면_쿼리가_실패해야한다() {
        //given
        queueService.enqueue(EnqueueCommand.from(1L));

        //when
        QueueQuery failQuery = QueueQuery.from("differentWaitId");
        //then
        assertThatThrownBy(() -> queueService.query(failQuery))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void Queue_만료된큐라면_쿼리가_실패해야한다() {
        //given
        queueService.enqueue(EnqueueCommand.from(1L));
        Queue queue = queueRepository.findByUserId(1L).orElseThrow();
        queue.activate(LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(23));
        queue.expire();
        queueRepository.save(queue);

        //when
        QueueQuery failQuery = QueueQuery.from(queue.getWaitId());

        //then
        assertThatThrownBy(() -> queueService.query(failQuery))
                .isInstanceOf(IllegalStateException.class);

    }

    @Test
    void 앞에10명이대기하고있고_새로활성화되려면_내순번은11번째다() {
        //given
        for (int i = 1; i <= 10; i++) {
            queueService.enqueue(EnqueueCommand.from((long) i));
        }
        Long queueId = queueService.enqueue(EnqueueCommand.from(11L)).userId();

        //when
        String waitId = queueRepository.findById(queueId).orElseThrow().getWaitId();
        QueueQueryResult myTurn = queueService.query(QueueQuery.from(waitId));

        //then
        assertThat(myTurn.position()).isEqualTo(11);
    }

    @Test
    void 앞에10명이활성중이고_새로활성화되려면_내순번은1번째다() {
        //given
        for (int i = 1; i <= 10; i++) {
            queueService.enqueue(EnqueueCommand.from((long) i));
            Queue queue = queueRepository.findByUserId((long) i).get();
            queue.activate(LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));
            queueRepository.save(queue);
        }

        Long queueId = queueService.enqueue(EnqueueCommand.from(11L)).userId();

        //when
        String waitId = queueRepository.findById(queueId).orElseThrow().getWaitId();
        QueueQueryResult myTurn = queueService.query(QueueQuery.from(waitId));

        //then
        assertThat(myTurn.position()).isEqualTo(1);

    }

//    @Test
//    void 앞에10명이대기하고있고_새로활성화되려면_최대20초는기다려야하고_내순번은11번째다() {
//        //given
//        for (int i = 0; i < 10; i++) {
//            queueService.enqueue(EnqueueCommand.from((long) i));
//        }
//        queueService.enqueue(EnqueueCommand.from(1L));
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
//    void 앞에10명이활성중이고_새로활성화되려면_최대10초는기다려야하고_내순번은1번째다() {
//        //given
//        for (int i = 0; i < 10; i++) {
//            queueService.enqueue(EnqueueCommand.from((long) i));
//            Queue queue = queueRepository.findByWaitId("userId"+i).get();
//            queue.activate(LocalDateTime.now());
//        }
//        queueService.enqueue(EnqueueCommand.from(1L));
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
