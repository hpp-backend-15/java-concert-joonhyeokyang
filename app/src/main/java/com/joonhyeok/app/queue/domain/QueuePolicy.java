package com.joonhyeok.app.queue.domain;

public interface QueuePolicy {
    public void activate();
    public void expire();
}
