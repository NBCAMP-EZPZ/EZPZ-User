package com.sparta.ezpzuser.domain.coupon.dto;

import com.sparta.ezpzuser.domain.coupon.entity.Coupon;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CouponResponseDto {

    private final Long couponId;
    private final String name;
    private final int discountAmount;
    private final int totalCount;
    private final LocalDate expiredAt;

    private CouponResponseDto(Coupon coupon) {
        this.couponId = coupon.getId();
        this.name = coupon.getName();
        this.discountAmount = coupon.getDiscountAmount();
        this.totalCount = coupon.getTotalCount();
        this.expiredAt = coupon.getExpiredAt();
    }

    public static CouponResponseDto of(Coupon coupon) {
        return new CouponResponseDto(coupon);
    }

}
