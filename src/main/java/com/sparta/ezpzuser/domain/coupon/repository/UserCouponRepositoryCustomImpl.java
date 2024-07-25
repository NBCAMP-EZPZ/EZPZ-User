package com.sparta.ezpzuser.domain.coupon.repository;

import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ezpzuser.domain.coupon.entity.Coupon;
import com.sparta.ezpzuser.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sparta.ezpzuser.common.util.RepositoryUtil.getTotal;
import static com.sparta.ezpzuser.domain.coupon.entity.QCoupon.coupon;
import static com.sparta.ezpzuser.domain.coupon.entity.QUserCoupon.userCoupon;

@Repository
@RequiredArgsConstructor
public class UserCouponRepositoryCustomImpl implements UserCouponRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Coupon> findAllMyCoupons(User user, Pageable pageable) {
        // 데이터 조회 쿼리
        List<Coupon> coupons = queryFactory
                .select(coupon)
                .from(userCoupon)
                .where(
                        userCoupon.user.eq(user)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트 쿼리
        Long totalCount = queryFactory
                .select(Wildcard.count)
                .from(userCoupon)
                .where(
                        userCoupon.user.eq(user)
                )
                .fetchOne();

        return PageableExecutionUtils.getPage(coupons, pageable, () -> getTotal(totalCount));
    }

}
