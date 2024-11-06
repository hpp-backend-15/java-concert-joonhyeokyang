package com.joonhyeok.app.common.config;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.Lock;

@Service
public class RedisTracingService {
    private final RedissonClient redissonClient;
    private final Tracer tracer;

    public RedisTracingService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
        this.tracer = GlobalOpenTelemetry.getTracer("redis-tracer");
    }


    public RLock getLock(String key) {
        Span span = tracer.spanBuilder("redis-set-key").startSpan();
        try {
            return redissonClient.getLock(key);
        } finally {
            span.end();
        }
    }
}