package com.sparta.ezpzuser.common.lock;

import com.sparta.ezpzuser.common.util.CustomSpELParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {

    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    private final TransactionForAop transactionForAop;

    @Around("@annotation(com.sparta.ezpzuser.common.lock.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        String key = REDISSON_LOCK_PREFIX +
                CustomSpELParser.getDynamicValue(
                        signature.getParameterNames(),
                        joinPoint.getArgs(),
                        distributedLock.key()
                );

        RLock rLock = redissonClient.getFairLock(key); // 선착순 보장

        try {
            log.info("try lock for key: {}", key);
            boolean available = rLock.tryLock(
                    distributedLock.waitTime(),
                    distributedLock.leaseTime(),
                    distributedLock.timeUnit()
            );
            if (!available) {
                throw new RuntimeException("lock failed for key: " + key);
            }
            // DistributedLock 어노테이션이 선언된 메서드를 별도의 트랜잭션으로 실행
            return transactionForAop.proceed(joinPoint);
        } catch (InterruptedException e) {
            throw new InterruptedException();
        } finally {
            // 반드시 트랜잭션 커밋 이후 락이 해제되도록 처리
            try {
                rLock.unlock();
            } catch (IllegalMonitorStateException e) {
                log.error("Redisson Lock is Already UnLocked");
            }
        }
    }

}
