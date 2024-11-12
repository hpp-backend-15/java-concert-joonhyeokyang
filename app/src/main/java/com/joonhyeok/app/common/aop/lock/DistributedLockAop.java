package com.joonhyeok.app.common.aop.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;
    private final SpelExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(distributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        String type = distributedLock.type();
        String keyExpression = distributedLock.key();
        String lockKey = parseKeyExpression(keyExpression, joinPoint);
        String key = REDISSON_LOCK_PREFIX.concat(":").concat(type).concat(":").concat(lockKey);
        RLock rLock = redissonClient.getLock(key);
        try {
            boolean available = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
            if (!available) {
                return false;
            }
            return aopForTransaction.proceed(joinPoint);
        } catch (InterruptedException e) {
            throw new InterruptedException();
        }
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