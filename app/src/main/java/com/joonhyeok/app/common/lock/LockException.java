package com.joonhyeok.app.common.lock;

public class LockException extends RuntimeException {
    public LockException() {
    }

    public LockException(String message) {
        super(message);
    }

    public LockException(Throwable cause) {
        super(cause);
    }
}
