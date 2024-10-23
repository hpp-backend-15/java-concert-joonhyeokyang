package com.joonhyeok.app.queue;

import com.joonhyeok.app.queue.appication.QueueService;
import com.joonhyeok.app.queue.appication.dto.EnqueueCommand;
import com.joonhyeok.app.queue.appication.dto.EnqueueResult;
import com.joonhyeok.app.queue.appication.dto.QueueQuery;
import com.joonhyeok.app.queue.appication.dto.QueueQueryResult;
import com.joonhyeok.app.queue.domain.Queue;
import com.joonhyeok.app.queue.domain.QueueRepository;
import com.joonhyeok.app.user.UserMemoryRepository;
import com.joonhyeok.app.user.domain.Account;
import com.joonhyeok.app.user.domain.User;
import com.joonhyeok.app.user.domain.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.joonhyeok.app.queue.domain.QueueStatus.WAIT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QueueServiceTest {

    QueueService queueService;
    QueueRepository queueRepository;
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        queueRepository = new QueueMemoryRepository();
        userRepository = new UserMemoryRepository();
        queueService = new QueueService(queueRepository, userRepository);
    }

    @Test
    void Queue_대기열_등록하는_경우_성공한다() {
        //given
        User user = new User(null, new Account(0L, LocalDateTime.now()), 0);
        userRepository.save(user);


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
        User user = new User(null, new Account(0L, LocalDateTime.now()), 0);
        userRepository.save(user);

        queueService.enqueue(EnqueueCommand.from(1L));

        //when
        EnqueueCommand duplicatedCommand = EnqueueCommand.from(1L);

        //then
        assertThatThrownBy(() -> queueService.enqueue(duplicatedCommand))
                .isInstanceOf(EntityExistsException.class);
    }

    @Test
    void 대기요청을_시도할시_존재하는_유저여야만한다() {
        //given
        EnqueueCommand duplicatedCommand = EnqueueCommand.from(1L);

        //when
        //then
        assertThatThrownBy(() -> queueService.enqueue(duplicatedCommand))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void Queue_이미대기하는_요청을_쿼리하면_찾을수있어야한다() {
        //given
        User user = new User(null, new Account(0L, LocalDateTime.now()), 0);
        userRepository.save(user);
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
        User user = new User(null, new Account(0L, LocalDateTime.now()), 0);
        userRepository.save(user);
        queueService.enqueue(EnqueueCommand.from(1L));

        //when
        QueueQuery failQuery = QueueQuery.from("differentWaitId");

        //then
        assertThatThrownBy(() -> queueService.query(failQuery))
                .isInstanceOf(EntityNotFoundException.class);
    }


    @Test
    void 앞에10명이대기하고있고_새로활성화되려면_내순번은11번째다() {
        //given
        for (int i = 1; i <= 10; i++) {
            User user = new User(null, new Account(0L, LocalDateTime.now()), 0);
            userRepository.save(user);
            queueService.enqueue(EnqueueCommand.from((long) i));
        }
        User user = new User(null, new Account(0L, LocalDateTime.now()), 0);
        userRepository.save(user);

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
            User user = new User(null, new Account(0L, LocalDateTime.now()), 0);
            userRepository.save(user);
            queueService.enqueue(EnqueueCommand.from((long) i));
            Queue queue = queueRepository.findByUserId((long) i).get();
            queue.activate(LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));
        }
        User user = new User(null, new Account(0L, LocalDateTime.now()), 0);
        userRepository.save(user);
        Long queueId = queueService.enqueue(EnqueueCommand.from(11L)).userId();

        //when
        String waitId = queueRepository.findById(queueId).orElseThrow().getWaitId();
        QueueQueryResult myTurn = queueService.query(QueueQuery.from(waitId));

        //then
        assertThat(myTurn.position()).isEqualTo(1);

    }


}
