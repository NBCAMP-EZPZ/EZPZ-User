package com.sparta.ezpzuser.domain.coupon.dto;

import com.sparta.ezpzuser.domain.coupon.entity.Coupon;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CouponResponseDto {

    private final Long id;
    private final String name;
    private final int discountAmount;
    private final LocalDate expiredAt;

    private CouponResponseDto(Coupon coupon) {
        this.id = coupon.getId();
        this.name = coupon.getName();
        this.discountAmount = coupon.getDiscountAmount();
        this.expiredAt = coupon.getExpiredAt();
    }

    public static CouponResponseDto of(Coupon coupon) {
        return new CouponResponseDto(coupon);
    }

}
