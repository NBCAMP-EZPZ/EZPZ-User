package com.sparta.ezpzuser.domain.coupon.service;

import com.sparta.ezpzuser.domain.coupon.entity.Coupon;
import com.sparta.ezpzuser.domain.coupon.repository.CouponRepository;
import com.sparta.ezpzuser.domain.coupon.repository.UserCouponRepository;
import com.sparta.ezpzuser.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
public class CouponConcurrencyTest {

    @Autowired
    CouponService couponService;

    @Autowired
    CouponRepository couponRepository;

    @MockBean
    UserCouponRepository userCouponRepository;

    User user = User.createMockUser();
    Coupon coupon;

    int threadCount = 100;

    @BeforeEach
    void setUp() {
        coupon = Coupon.createMockCoupon(threadCount);
        couponRepository.save(coupon);
    }

    @Test
    void 쿠폰_다운로드_동시성_테스트_분산락_미적용() {
        // given
        given(userCouponRepository.existsByUserAndCoupon(any(User.class), any(Coupon.class)))
                .willReturn(false);

        // when
        IntStream.range(0, threadCount).parallel().forEach(i ->
                couponService.downloadCouponWithoutLock(coupon.getId(), user)
        );

        // then
        int remainingCount = couponRepository.findById(coupon.getId()).orElseThrow().getRemainingCount();
        assertThat(remainingCount).isNotZero().isNotEqualTo(threadCount);

        System.out.println("\n[remainingCount]");
        System.out.println("Expected = 0");
        System.out.println("Actual = " + remainingCount);
    }

    @Test
    void 쿠폰_다운로드_동시성_테스트_분산락_적용() {
        // given
        given(userCouponRepository.existsByUserAndCoupon(any(User.class), any(Coupon.class)))
                .willReturn(false);

        // when
        IntStream.range(0, threadCount).parallel().forEach(i ->
                couponService.downloadCoupon(coupon.getId(), user)
        );

        // then
        int remainingCount = couponRepository.findById(coupon.getId()).orElseThrow().getRemainingCount();
        assertThat(remainingCount).isZero();

        System.out.println("\n[remainingCount]");
        System.out.println("Expected = 0");
        System.out.println("Actual = " + remainingCount);
    }

}
