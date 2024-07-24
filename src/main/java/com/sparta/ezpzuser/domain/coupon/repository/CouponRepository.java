package com.sparta.ezpzuser.domain.coupon.repository;

import com.sparta.ezpzuser.domain.coupon.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Page<Coupon> findByRemainingCountGreaterThan(int i, Pageable pageable);

}
