package com.joonhyeok.app.user.domain.user;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findWithLockById(Long id);
    Optional<User> findById(Long id);
    User save(User user);
}
