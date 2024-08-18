package com.sparta.ezpzuser.domain.coupon.repository;

import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ezpzuser.domain.coupon.entity.Coupon;
import com.sparta.ezpzuser.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.sparta.ezpzuser.common.util.RepositoryUtil.getTotal;
import static com.sparta.ezpzuser.domain.coupon.entity.QCoupon.coupon;
import static com.sparta.ezpzuser.domain.coupon.entity.QUserCoupon.userCoupon;

@RequiredArgsConstructor
public class CouponRepositoryCustomImpl implements CouponRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Coupon> findAllDownloadableCoupons(User user, Pageable pageable) {
        List<Coupon> coupons = queryFactory
                .selectFrom(coupon)
                .leftJoin(userCoupon)
                .on(coupon.id.eq(userCoupon.coupon.id))
                .where(
                        userCoupon.user.ne(user)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(Wildcard.count)
                .from(coupon)
                .leftJoin(userCoupon)
                .on(coupon.id.eq(userCoupon.coupon.id))
                .where(
                        userCoupon.user.ne(user)
                )
                .fetchOne();

        return PageableExecutionUtils.getPage(coupons, pageable, () -> getTotal(totalCount));
    }

}
