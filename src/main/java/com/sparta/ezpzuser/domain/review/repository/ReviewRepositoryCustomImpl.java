package com.sparta.ezpzuser.domain.review.repository;

import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ezpzuser.domain.user.entity.User;
import lombok.RequiredArgsConstructor;

import static com.sparta.ezpzuser.common.util.RepositoryUtil.getTotal;
import static com.sparta.ezpzuser.domain.review.entity.QReview.review;

@RequiredArgsConstructor
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsByUser(User user) {
        Long totalCount = queryFactory
                .select(Wildcard.count)
                .from(review)
                .where(
                        review.reservation.user.eq(user)
                )
                .fetchOne();

        return getTotal(totalCount) > 0L;
    }

}
