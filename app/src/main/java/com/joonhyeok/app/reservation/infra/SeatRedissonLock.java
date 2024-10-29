package com.joonhyeok.app.reservation.infra;

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
public class SeatRedissonLock implements LockManager {
    long waitTime = 10L;
    long leaseTime = 9L;
    TimeUnit timeUnit = TimeUnit.SECONDS;
    private final RedissonClient redissonClient;

    @Override
    public LockId tryLock(String type, Long id) throws LockException {
        log.info("tryLock type = {}, id = {}", type, id);
        RLock rLock = redissonClient.getLock(type + "-" + id);
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
        return new LockId(type + "-" + id);
    }

    @Override
    public void releaseLock(LockId lockId) throws LockException {
        log.info("releaseLock lockId = {}", lockId);
        RLock rLock = redissonClient.getLock(lockId.getValue());
        try {
            rLock.unlock();
        } catch (IllegalMonitorStateException e) {
            log.error("releaseLock fail, lockId = {}", lockId);
            throw new NoLockException();
        }
    }

}
