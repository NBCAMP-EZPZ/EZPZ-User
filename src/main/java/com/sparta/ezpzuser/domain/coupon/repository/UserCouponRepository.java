package com.sparta.ezpzuser.domain.coupon.repository;

import com.sparta.ezpzuser.domain.coupon.entity.Coupon;
import com.sparta.ezpzuser.domain.coupon.entity.UserCoupon;
import com.sparta.ezpzuser.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long>, UserCouponRepositoryCustom {

    boolean existsByUserAndCoupon(User user, Coupon coupon);

}
