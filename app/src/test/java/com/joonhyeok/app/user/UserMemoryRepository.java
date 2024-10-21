package com.joonhyeok.app.user;


import java.util.HashMap;
import java.util.Optional;

public class UserMemoryRepository implements UserRepository {
    private HashMap<Long, User> map = new HashMap<>();
    private Long id = 1L;

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
