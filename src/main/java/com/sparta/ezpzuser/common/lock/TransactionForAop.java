package com.sparta.ezpzuser.common.lock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * AOP 에서 트랜잭션 분리를 위한 클래스
 */
@Component
public class TransactionForAop {

    // 부모 트랜잭션에 상관없이 별도의 트랜잭션으로 동작
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }

}