package com.joonhyeok.app.common.aop.token;

import com.joonhyeok.app.queue.domain.Queue;
import com.joonhyeok.app.queue.domain.QueueRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@Aspect
@RequiredArgsConstructor
public class VerifyWaitAspect {

    private final QueueRepository queueRepository;

    @Pointcut("@annotation(com.joonhyeok.app.common.aop.token.VerifyWait)")
    public void verifyTokenPointcut() {
    }

    @Before("verifyTokenPointcut()")
    public void verifyToken(JoinPoint joinPoint) {
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();

        String waitId = request.getHeader("Wait-Token");
        Queue queue = queueRepository.findByWaitId(waitId).orElseThrow(() -> new EntityNotFoundException("대기 이력이 없는 이용자입니다."));

        if (!queue.isActive()) {
            throw new IllegalStateException("대기중이거나 만료된 토큰입니다.");
        }
    }
}