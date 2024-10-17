package com.joonhyeok.app.queue.domain;

import com.joonhyeok.app.common.Constants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.joonhyeok.app.queue.domain.QueueStatus.*;
import static jakarta.persistence.EnumType.STRING;
import static java.lang.Math.ceil;
import static java.lang.Math.max;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class Queue {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Enumerated(STRING)
    private QueueStatus status;

    private LocalDateTime issueAt = LocalDateTime.now();
    private LocalDateTime enteredAt;
    private LocalDateTime expireAt;
    private LocalDateTime lastRequestedAt;


    public boolean isExpired() {
        return status == EXPIRED;
    }
    public void activate(LocalDateTime enteredAt) {
        if (status != WAIT) {
            throw new IllegalStateException("cannot change queue status to ACTIVATE, current status is " + status);
        }
        this.status = ACTIVE;
        this.enteredAt = enteredAt;
        this.expireAt = enteredAt.plusMinutes(10);
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

    public static Queue create(String sessionId) {
        Queue queue = new Queue();
        queue.sessionId = sessionId;
        queue.status = WAIT;
        return queue;
    }

    public Long getPosition(Long lastActivatePosition) {
        return id - lastActivatePosition;
    }

    public LocalDateTime getEstimateWaitTime(Long lastActivatePosition) {
        long unitOfWaiting = getPosition(lastActivatePosition) + 1 / Constants.SCHEDULER_ACTIVATE_QUANTITY_IN_PERIODS;
        long totalWaitingSeconds = (long) ceil(unitOfWaiting) * Constants.SCHEDULER_ACTIVATE_PERIOD_IN_SECONDS;
        return LocalDateTime.now().plusSeconds(totalWaitingSeconds);
    }
}
