package com.joonhyeok.app.common.aop.lock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * AOP에서 트랜잭션 분리를 위한 클래스
 *
 * 트랜잭션을 분리하는 이유
 * 락 획득 - TX 시작 - TX 종료 - 락 해제의 흐름을 관리하기 위해
 * (즉, 분산락과는 다른 스레드를 이용하고, 예외를 처리하기 위해)
 */
@Component
public class AopForTransaction {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }
}