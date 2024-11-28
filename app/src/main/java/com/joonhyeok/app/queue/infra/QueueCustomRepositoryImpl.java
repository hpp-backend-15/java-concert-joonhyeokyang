package com.joonhyeok.app.queue.infra;

import com.joonhyeok.app.queue.domain.Queue;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.util.Optional;

@RequiredArgsConstructor
public class QueueCustomRepositoryImpl implements QueueCustomRepository {
    private final RedissonClient redissonClient;

    @Override
    public Optional<Long> findMaxPositionOfActivated() {
        // RMap 초기화
        RMap<Long, Queue> queueMap = redissonClient.getMap("QueueMap");

        // Map에서 데이터를 필터링하여 최대값 계산
        return null;
    }
}