package com.sparta.ezpzuser.domain.order.service;

import com.sparta.ezpzuser.domain.item.repository.ItemRepository;
import com.sparta.ezpzuser.domain.order.dto.OrderRequestDto;
import com.sparta.ezpzuser.domain.order.dto.OrderRequestDto.CartIdRequest;
import com.sparta.ezpzuser.domain.order.dto.OrderResponseDto;
import com.sparta.ezpzuser.domain.user.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class OrderServiceConcurrencyTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ItemRepository itemRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User(
                2003L,
                "useruseraa1",
                "$2a$10$IDFtfqng8SA4PdbJi.2UMur1OnmWQloswZ7mQaZSh3zBYSCRwNpmW",
                "user1",
                "user1@naver.com",
                "01011111111",
                new ArrayList<>()
        );
    }

    @Test
    @DisplayName("Test 동시성 주문 생성")
    @Rollback
    void testConcurrentOrderCreation() throws InterruptedException {
        int userCount = 100;
        int baseCartId = 50099;
        
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        ExecutorService executorService = Executors.newFixedThreadPool(userCount);
        CountDownLatch latch = new CountDownLatch(userCount);

        for (int i = 1; i <= userCount; i++) {
            final int cartId = baseCartId + i - 1;
            executorService.submit(() -> {
                try {
                    OrderRequestDto requestDto = new OrderRequestDto(
                            List.of(new CartIdRequest((long) cartId))
                    );

                    OrderResponseDto responseDto = orderService.createOrder(requestDto, user);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        System.out.println("successCount = " + successCount);
        System.out.println("failCount = " + failCount);
    }
}
