package com.joonhyeok.app.queue.infra;

import com.joonhyeok.app.queue.domain.Queue;
import com.joonhyeok.app.queue.domain.QueueRepository;
import org.springframework.data.repository.Repository;

public interface QueueJpaRepository extends QueueRepository, Repository<Queue, Long> {
}
