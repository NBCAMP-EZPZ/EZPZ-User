package com.sparta.ezpzuser.domain.coupon.entity;

import com.sparta.ezpzuser.common.exception.CustomException;
import org.junit.jupiter.api.Test;

import static com.sparta.ezpzuser.common.exception.ErrorType.SOLD_OUT_COUPON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CouponTest {

    @Test
    void 쿠폰_다운로드_성공() {
        // given
        int couponCount = 10;
        Coupon coupon = Coupon.createMockCoupon(couponCount);

        // when
        coupon.download();

        // then
        assertThat(coupon.getTotalCount()).isEqualTo(couponCount);
        assertThat(coupon.getRemainingCount()).isEqualTo(couponCount - 1);
    }

    @Test
    void 쿠폰_다운로드_실패() {
        // given
        int couponCount = 0;
        Coupon coupon = Coupon.createMockCoupon(couponCount);

        // when
        CustomException exception = assertThrows(CustomException.class, coupon::download);

        // then
        assertThat(exception.getErrorType()).isEqualTo(SOLD_OUT_COUPON);
    }

}