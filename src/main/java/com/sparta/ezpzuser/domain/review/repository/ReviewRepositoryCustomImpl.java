package com.sparta.ezpzuser.domain.review.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ezpzuser.domain.reservation.entity.Reservation;
import lombok.RequiredArgsConstructor;

import static com.sparta.ezpzuser.domain.review.entity.QReview.review;

@RequiredArgsConstructor
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsByReservation(Reservation reservation) {
        return queryFactory
                .selectOne()
                .from(review)
                .where(
                        review.popup.eq(reservation.getSlot().getPopup()),
                        review.reservation.user.eq(reservation.getUser())
                )
                .fetchFirst() != null;
    }

}
