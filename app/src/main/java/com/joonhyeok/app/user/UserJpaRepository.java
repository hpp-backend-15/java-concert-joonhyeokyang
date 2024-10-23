package com.joonhyeok.app.user;

import com.joonhyeok.app.user.domain.User;
import com.joonhyeok.app.user.domain.UserRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface UserJpaRepository extends UserRepository, Repository<User, Long> {
    @Override
    @Lock(LockModeType.OPTIMISTIC)
    Optional<User> findWithLockById(Long id);
}
