package com.joonhyeok.app.reservation.infra;

import com.joonhyeok.app.common.lock.*;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedissonLockManager implements LockManager {
    long waitTime = 100L;
    long leaseTime = 50_000L;
    TimeUnit timeUnit = TimeUnit.MILLISECONDS;
    private final RedissonClient redissonClient;
    private final Tracer tracer;

    //TODO Tracer AOP 분리
    @Override
    public LockId tryLock(String type, Long id) throws LockException {
        log.info("tryLock type = {}, id = {}", type, id);
        LockId lockId = new LockId(type + "-" + id);
        RLock rLock = redissonClient.getLock(lockId.getValue());
        Span span = tracer.spanBuilder("RedissonLockManager.tryLock").startSpan();
        span.setAttribute("lock.type", type);
        span.setAttribute("lock.id", id.toString());
        boolean gotLock = false;
        try {
            gotLock = rLock.tryLock(waitTime, leaseTime, timeUnit);
            if (!gotLock) {
                span.setAttribute("lock.success", false);
                log.info("tryLock fail, waitTime = {}, leaseTime = {}", waitTime, leaseTime);
                throw new AlreadyLockedException();
            }
            span.setAttribute("lock.success", true);
            log.info("tryLock success, waitTime = {}, leaseTime = {}", waitTime, leaseTime);
        } catch (InterruptedException e) {
            span.recordException(e);
            log.info("tryLock interrupted, waitTime = {}, leaseTime = {}", waitTime, leaseTime);
            throw new LockingFailException(e);
        } finally {
            span.end();
        }
        return lockId;
    }

    @Override
    public void releaseLock(LockId lockId) throws LockException {
        Span span = tracer.spanBuilder("RedissonLockManager.releaseLock").startSpan();
        span.setAttribute("lock.name", lockId.getValue());
        log.info("releaseLock lockId = {}", lockId.getValue());
        RLock rLock = redissonClient.getLock(lockId.getValue());
        try {
            if (rLock.isHeldByCurrentThread() && rLock.isLocked()) {
                span.setAttribute("lock.released", true);
                rLock.unlock();
            }
        } catch (IllegalMonitorStateException e) {
            span.recordException(e);
            log.error("releaseLock fail, lockId = {}", lockId.getValue());
            throw new NoLockException();
        } finally {
            span.end();
        }
    }

}
