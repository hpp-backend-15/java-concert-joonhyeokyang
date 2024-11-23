package com.joonhyeok.app.user;


import com.joonhyeok.app.user.domain.user.User;
import com.joonhyeok.app.user.domain.user.UserRepository;

import java.util.HashMap;
import java.util.Optional;

public class UserMemoryRepository implements UserRepository {
    private HashMap<Long, User> map = new HashMap<>();
    private Long id = 1L;

    @Override
    public Optional<User> findWithLockById(Long id) {
        return findById(id);
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(map.get(id));
    }

    public User save(User user) {
        User newUser = new User(id, user.getAccount(), user.getVersion());
        map.put(id, newUser);
        id = id + 1;
        return newUser;
    }
}
