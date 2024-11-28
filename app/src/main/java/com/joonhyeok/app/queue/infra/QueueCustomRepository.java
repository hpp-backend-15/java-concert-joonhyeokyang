package com.joonhyeok.app.queue.infra;

import java.util.Optional;

public interface QueueCustomRepository {
    Optional<Long> findMaxPositionOfActivated();
}