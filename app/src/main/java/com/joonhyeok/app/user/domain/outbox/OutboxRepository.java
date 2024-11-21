package com.joonhyeok.app.user.domain.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OutboxRepository extends JpaRepository<Outbox, Long> {
    Optional<Outbox> findOutboxByTypeAndRelationalId(String type, Long relationalId);
}
