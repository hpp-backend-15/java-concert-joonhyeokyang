package com.joonhyeok.app.queue.domain;

import com.joonhyeok.app.common.Constants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.joonhyeok.app.queue.domain.QueueStatus.*;
import static jakarta.persistence.EnumType.STRING;
import static java.lang.Math.ceil;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "queues")
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class Queue {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "queues_wait_id", nullable = false)
    private String waitId;

    @Column(name = "queues_user_id", nullable = false)
    private String userId;

    @Enumerated(STRING)
    private QueueStatus status;

    private LocalDateTime issueAt = LocalDateTime.now();
    private LocalDateTime enteredAt;
    private LocalDateTime expireAt;
    private LocalDateTime lastRequestedAt;


    public boolean isExpired() {
        return status == EXPIRED;
    }

    public boolean isActive() {
        return status == ACTIVE;
    }

    public void activate(LocalDateTime enteredAt, LocalDateTime expireAt) {
        if (status != WAIT) {
            throw new IllegalStateException("cannot change queue status to ACTIVE, current status is " + status);
        }
        this.status = ACTIVE;
        this.enteredAt = enteredAt;
        this.expireAt = expireAt;
    }

    public void expire() {
        if (status != ACTIVE) {
            throw new IllegalStateException("cannot change queue status to EXPIRE, current status is " + status);
        }

        // 아직 최소 만료시간이 되지 않은 경우
        if (expireAt.isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("cannot change queue status to EXPIRE, will be expired At " + expireAt);
        }
        this.status = EXPIRED;
    }

    public static Queue create(String userId) {
        Queue queue = new Queue();
        queue.waitId = UUID.randomUUID().toString();
        queue.userId = userId;
        queue.status = WAIT;
        return queue;
    }

    public Long getPosition(Long lastActivatePosition) {
        return id - lastActivatePosition;
    }

    public LocalDateTime getEstimateWaitTime(Long lastActivatePosition) {
        long unitOfWaiting = getPosition(lastActivatePosition) + 1 / Constants.SCHEDULER_ACTIVE_QUANTITY_IN_PERIODS;
        long totalWaitingSeconds = (long) ceil(unitOfWaiting) * Constants.SCHEDULER_ACTIVE_PERIOD_IN_SECONDS;
        return LocalDateTime.now().plusSeconds(totalWaitingSeconds);
    }
}
