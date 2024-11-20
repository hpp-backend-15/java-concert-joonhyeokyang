package com.joonhyeok.app.user.infra.domain.payEvent;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OutBoxRepository extends JpaRepository<OutBox, Long> {
    Optional<OutBox> findOutBoxByTypeAndRelationalId(String pay, Long relationalId);
}
