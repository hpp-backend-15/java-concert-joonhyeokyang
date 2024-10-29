package com.joonhyeok.app.common.lock;

public class LockingFailException extends LockException {
    public LockingFailException() {
    }

    public LockingFailException(Exception cause) {
        super(cause);
    }
}
