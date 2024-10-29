package com.joonhyeok.app.common.lock;

public interface LockManager {
    LockId tryLock(String type, Long id) throws LockException;

    void releaseLock(LockId lockId) throws LockException;

}
