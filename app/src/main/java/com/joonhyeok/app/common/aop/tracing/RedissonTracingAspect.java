//package com.joonhyeok.app.common.aop.tracing;
//
//import io.opentelemetry.api.GlobalOpenTelemetry;
//import io.opentelemetry.api.trace.Span;
//import io.opentelemetry.api.trace.Tracer;
//import io.opentelemetry.context.Scope;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.After;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.springframework.stereotype.Component;
//
//import java.util.Arrays;
//
//@Aspect
//@Component
//@Slf4j
//public class RedissonTracingAspect {
//
//    private final Tracer tracer;
//
//    public RedissonTracingAspect() {
//        this.tracer = GlobalOpenTelemetry.getTracer("my.redisson.client");
//    }
//
//    // RedissonClient의 모든 메서드 호출에 대해 트레이스를 생성
//    @Around("execution(* org.redisson.api.RedissonClient.*(..))")
//    public void beforeRedissonMethod(JoinPoint joinPoint) throws NoSuchFieldException, IllegalAccessException {
//        log.info("tracing redisson starts");
//        // 메서드 이름을 이용해 Span 이름을 설정
//        String methodName = joinPoint.getSignature().getName();
//
//        // Span을 시작
//        Span span = tracer.spanBuilder("Redisson." + methodName)
//                .startSpan();
//
//        // Span에 메타데이터 추가 (예: Redis 키 이름 등)
//        span.setAttribute("redis.method", methodName);
//        span.setAttribute("redis.args", Arrays.toString(joinPoint.getArgs()));
//
//        // 현재 스레드에 Span을 설정
//        Scope scope = span.makeCurrent();
//        // 메서드가 끝나면 Span을 종료하도록 보장하기 위해 scope를 저장
//        joinPoint.getTarget().getClass().getDeclaredField("spanScope").set(joinPoint.getTarget(), scope);
//    }
//
//    // 메서드 실행 후 Span 종료
//    @After("execution(* org.redisson.api.RedissonClient.*(..))")
//    public void afterRedissonMethod(JoinPoint joinPoint) {
//        try {
//            log.info("tracing redisson end");
//            // Span 종료
//            Span span = (Span) joinPoint.getTarget().getClass().getDeclaredField("span").get(joinPoint.getTarget());
//            if (span != null) {
//                span.end();
//            }
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
//    }
//}
