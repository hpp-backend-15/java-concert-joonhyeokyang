package com.joonhyeok.app.common.aop.lock;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @DistributedLock 선언 시 수행되는 Aop class
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class DistributedLockAop {
    private static final String REDISSON_LOCK_PREFIX = "LOCK";

    private final Tracer tracer;
    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;
    private final SpelExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(distributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        Span span = tracingStart(joinPoint);

        String type = distributedLock.type();
        String keyExpression = distributedLock.key();
        String lockKey = parseKeyExpression(keyExpression, joinPoint);
        String key = REDISSON_LOCK_PREFIX.concat(":").concat(type).concat(":").concat(lockKey);
        RLock rLock = redissonClient.getLock(key);
        try {
            boolean available = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
            if (!available) {
                span.setAttribute("distributedLock.success", false);
                return false;
            }
            span.setAttribute("distributedLock.success", true);

            span.addEvent("Lock acquired successfully");

            Object proceed = joinPoint.proceed();

            // 트래킹 종료
            tracingEnd(span);
            return proceed;
        } catch (InterruptedException e) {
            throw new InterruptedException();
        }
    }

    private void tracingEnd(Span span) {
        log.info("End Tracing...");
        if (span != null) {
            span.addEvent("Lock released");
            span.end();
        } else {
            log.info("No Tracking Span...");
        }
    }

    private Span tracingStart(ProceedingJoinPoint joinPoint) throws NoSuchFieldException, IllegalAccessException {
        String methodName = joinPoint.getSignature().getName();

        log.info("start Tracing...");
        // Span을 시작
        Span span = tracer.spanBuilder("distributedLock.startLock")
                .startSpan();

        // Span에 메타데이터 추가 (예: Redis 키 이름 등)
        span.setAttribute("redis.method", methodName);
        span.setAttribute("redis.args", Arrays.toString(joinPoint.getArgs()));
        span.setAttribute("distributedLock.success", true);
        return span;
    }

    private String parseKeyExpression(String keyExpression, ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        ParameterNameDiscoverer paramDiscoverer = new DefaultParameterNameDiscoverer();
        String[] parameterNames = paramDiscoverer.getParameterNames(method);
        Object[] parameterValues = joinPoint.getArgs();

        if (parameterNames == null) {
            throw new IllegalStateException("Parameter names could not be resolved.");
        }

        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], parameterValues[i]);
        }
        Expression expression = parser.parseExpression(keyExpression);
        Object value = expression.getValue(context);

        if (value == null) {
            throw new IllegalArgumentException("Invalid key expression in @DistributedLock");
        }
        return value.toString();
    }
}