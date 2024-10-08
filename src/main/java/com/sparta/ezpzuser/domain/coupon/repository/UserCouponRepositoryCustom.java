package com.sparta.ezpzuser.domain.coupon.repository;

import com.sparta.ezpzuser.domain.coupon.entity.Coupon;
import com.sparta.ezpzuser.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserCouponRepositoryCustom {

    Page<Coupon> findAllMyCoupons(User user, Pageable pageable);

}
