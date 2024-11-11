package com.joonhyeok.app.queue.infra;

import com.joonhyeok.app.queue.domain.Queue;
import com.joonhyeok.app.queue.domain.QueueRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface QueueJpaRepository extends QueueRepository, JpaRepository<Queue, Long> {
    @Override
    @Query("select max(q.id) " +
            "from Queue q " +
            "where q.status = 'ACTIVE'")
    Optional<Long> findMaxPositionOfActivated();
}
