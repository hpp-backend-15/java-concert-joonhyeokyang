package com.joonhyeok.app.reservation.infra;

import com.joonhyeok.app.common.config.RedisTracingService;
import com.joonhyeok.app.common.lock.*;
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
    long waitTime = 900L;
    long leaseTime = 300L;
    TimeUnit timeUnit = TimeUnit.MICROSECONDS;
    private final RedisTracingService redissonClient;

    @Override
    public LockId tryLock(String type, Long id) throws LockException {
        log.info("tryLock type = {}, id = {}", type, id);
        LockId lockId = new LockId(type + "-" + id);
        RLock rLock = redissonClient.getLock(lockId.getValue());
        boolean gotLock = false;
        try {
            gotLock = rLock.tryLock(waitTime, leaseTime, timeUnit);
            if (!gotLock) {
                log.info("tryLock fail, waitTime = {}, leaseTime = {}", waitTime, leaseTime);
                throw new AlreadyLockedException();
            }
            log.info("tryLock success, waitTime = {}, leaseTime = {}", waitTime, leaseTime);
        } catch (InterruptedException e) {
            log.info("tryLock interrupted, waitTime = {}, leaseTime = {}", waitTime, leaseTime);
            throw new LockingFailException(e);
        }
        return lockId;
    }

    @Override
    public void releaseLock(LockId lockId) throws LockException {
        log.info("releaseLock lockId = {}", lockId.getValue());
        RLock rLock = redissonClient.getLock(lockId.getValue());
        try {
            if (rLock.isHeldByCurrentThread() && rLock.isLocked()) {
                rLock.unlock();
            }
        } catch (IllegalMonitorStateException e) {
            log.error("releaseLock fail, lockId = {}", lockId.getValue());
            throw new NoLockException();
        }
    }

}
