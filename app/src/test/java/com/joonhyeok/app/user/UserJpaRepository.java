package com.joonhyeok.app.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

public interface UserJpaRepository extends UserRepository, Repository<User, Long> {
}
