package com.sparta.ezpzuser.domain.coupon.service;

import com.sparta.ezpzuser.domain.coupon.entity.Coupon;
import com.sparta.ezpzuser.domain.coupon.entity.UserCoupon;
import com.sparta.ezpzuser.domain.coupon.repository.CouponRepository;
import com.sparta.ezpzuser.domain.coupon.repository.UserCouponRepository;
import com.sparta.ezpzuser.domain.user.dto.SignupRequestDto;
import com.sparta.ezpzuser.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
public class CouponServiceTest {

    @Autowired
    CouponService couponService;

    @Autowired
    CouponRepository couponRepository;

    @MockBean
    UserCouponRepository userCouponRepository;

    Coupon coupon;
    User user;

    @BeforeEach
    void setUp() {
        coupon = couponRepository.save(Coupon.of(100));
        user = User.of(new SignupRequestDto(), "password");
    }

    @Test
    @DisplayName("실패 - 쿠폰 다운로드 동시성 테스트")
    void downloadCouponWithoutConcurrencyLock() {
        // given
        given(userCouponRepository.existsByUserAndCoupon(any(User.class), any(Coupon.class)))
                .willReturn(false);
        given(userCouponRepository.save(any(UserCoupon.class)))
                .willReturn(UserCoupon.of(
                        User.of(new SignupRequestDto(), "password"),
                        Coupon.of(100)));

        // when
        IntStream.range(0, 100).parallel()
                .forEach(i -> couponService.downloadCouponWithoutLock(coupon.getId(), user));

        // then
        int remainingCount = couponRepository.findById(coupon.getId()).orElseThrow().getRemainingCount();
        assertThat(remainingCount).isNotEqualTo(0);
        System.out.println("\n[remainingCount]");
        System.out.println("Expected = 0");
        System.out.println("Actual = " + remainingCount);
    }

    @Test
    @DisplayName("성공 - 쿠폰 다운로드 동시성 테스트")
    void downloadCouponWithConcurrencyLock() {
        // given
        given(userCouponRepository.existsByUserAndCoupon(any(User.class), any(Coupon.class)))
                .willReturn(false);
        given(userCouponRepository.save(any(UserCoupon.class)))
                .willReturn(UserCoupon.of(
                        User.of(new SignupRequestDto(), "password"),
                        Coupon.of(100)));

        // when
        IntStream.range(0, 100).parallel()
                .forEach(i -> couponService.downloadCoupon(coupon.getId(), user));

        // then
        int remainingCount = couponRepository.findById(coupon.getId()).orElseThrow().getRemainingCount();
        assertThat(remainingCount).isEqualTo(0);
    }

}
