package com.joonhyeok.app.queue;

import com.joonhyeok.app.queue.domain.Queue;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.joonhyeok.app.queue.domain.QueueStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class QueueTest {

    @Test
    void Queue_대기자_활성화하여_입장하는_경우() throws Exception {
        //given
        LocalDateTime now = LocalDateTime.now();
        Queue queue = new Queue(0L, "waitId", WAIT, LocalDateTime.now(), null, null, null);

        //when
        queue.activate(now);

        //then
        assertThat(queue.getStatus()).isEqualTo(ACTIVE);
        assertThat(queue.getEnteredAt()).isEqualTo(now);
        assertThat(queue.getExpireAt()).isEqualTo(now.plusMinutes(10));
    }

    @Test
    void Queue_이미_만료된_토큰을_활성화하는경우_예외() throws Exception {
        //given
        Queue queue = new Queue(0L, "waitId", EXPIRED, LocalDateTime.now(), null, null, null);

        //when
        //then
        assertThatThrownBy(() -> queue.activate(LocalDateTime.now()))
                .isInstanceOf(IllegalStateException.class);

    }

    @Test
    void Queue_만료기능_만료가_불가능한시간에_만료시키는경우() throws Exception {
        //given
        Queue queue = new Queue(0L, "waitId", WAIT, LocalDateTime.now(), null, null, null);

        //when
        queue.activate(LocalDateTime.now().minusMinutes(9));

        //then
        assertThatThrownBy(queue::expire)
                .isInstanceOf(IllegalStateException.class);

    }

    @Test
    void Queue_만료기능_만료가_가능한시간에_만료시키는경우() throws Exception {
        //given
        Queue queue = new Queue(0L, "waitId", WAIT, LocalDateTime.now(), null, null, null);

        //when
        queue.activate(LocalDateTime.now().minusMinutes(11));
        queue.expire();

        //then
        assertThat(queue.getStatus()).isEqualTo(EXPIRED);
    }
}
