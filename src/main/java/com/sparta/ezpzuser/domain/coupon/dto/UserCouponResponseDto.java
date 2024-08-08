package com.sparta.ezpzuser.domain.coupon.dto;

import com.sparta.ezpzuser.domain.coupon.entity.UserCoupon;
import lombok.Getter;

@Getter
public class UserCouponResponseDto {

    private final Long userCouponId;
    private final Long userId;
    private final Long couponId;

    private UserCouponResponseDto(UserCoupon userCoupon) {
        this.userCouponId = userCoupon.getId();
        this.userId = userCoupon.getUser().getId();
        this.couponId = userCoupon.getCoupon().getId();
    }

    public static UserCouponResponseDto of(UserCoupon userCoupon) {
        return new UserCouponResponseDto(userCoupon);
    }

}
