package com.joonhyeok.app.queue.domain;

import com.joonhyeok.app.common.Constants;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.joonhyeok.app.queue.domain.QueueStatus.*;
import static jakarta.persistence.EnumType.STRING;
import static java.lang.Math.ceil;
import static lombok.AccessLevel.PROTECTED;

@Slf4j
//@Entity
//@Table(name = "queues")
@RedisHash("Queue")
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class Queue {
    @Id
    private Long id;

    private String waitId;

    private Long userId;

    @Enumerated(STRING)
    private QueueStatus status;


    private LocalDateTime issueAt = LocalDateTime.now();
    private LocalDateTime enteredAt;
    private LocalDateTime expireAt;
    private LocalDateTime lastRequestedAt;

    @TimeToLive
    private long ttl;


    public boolean isExpired() {
        return status == EXPIRED;
    }

    public boolean isActive() {
        return status == ACTIVE;
    }

    public void activate(LocalDateTime enteredAt, LocalDateTime expireAt) {
        log.info("activate queue userId = {}, waitId = {}, enteredAt = {} will be expired at = {}",
                this.userId, this.waitId, this.enteredAt, this.expireAt);
        if (status != WAIT) {
            log.error("cannot change queue status to ACTIVE, current status is {}", this.status);
            throw new IllegalStateException("cannot change queue status to ACTIVE, current status is {}" + this.status);
        }
        this.status = ACTIVE;
        this.enteredAt = enteredAt;
        this.expireAt = expireAt;
    }

    public void invalidate() {
        this.status = EXPIRED;
    }

    public void expire() {
        log.info("expire queue userId = {}, waitId = {}, enteredAt = {} will be expired at = {}",
                this.userId, this.waitId, this.enteredAt, this.expireAt);

        if (status != ACTIVE) {
            log.error("cannot change queue status to EXPIRE, current status is {}", this.status);
            throw new IllegalStateException("cannot change queue status to EXPIRE, current status is " + status);
        }

        // 아직 최소 만료시간이 되지 않은 경우
        if (expireAt.isAfter(LocalDateTime.now())) {
            log.error("cannot change queue status to EXPIRE, will be expired At {}", expireAt);
            throw new IllegalStateException("cannot change queue status to EXPIRE, will be expired At " + expireAt);
        }
        this.status = EXPIRED;
    }

    public static Queue create(Long userId) {
        Queue queue = new Queue();
        queue.waitId = UUID.randomUUID().toString();
        queue.userId = userId;
        queue.status = WAIT;
        log.info("issue queue userId = {}, waitId = {}, issuedAt = {}", queue.userId, queue.waitId, queue.issueAt);
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
