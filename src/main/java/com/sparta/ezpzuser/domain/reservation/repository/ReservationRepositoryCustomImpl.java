package com.sparta.ezpzuser.domain.reservation.repository;

import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ezpzuser.domain.reservation.entity.Reservation;
import com.sparta.ezpzuser.domain.reservation.enums.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.sparta.ezpzuser.common.util.RepositoryUtil.getTotal;
import static com.sparta.ezpzuser.domain.reservation.entity.QReservation.reservation;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryCustomImpl implements ReservationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsByUserIdAndPopupId(Long userId, Long popupId) {
        return queryFactory
                .selectOne()
                .from(reservation)
                .where(reservation.user.id.eq(userId)
                        .and(reservation.reservationStatus.ne(ReservationStatus.CANCEL))
                        .and(reservation.slot.popup.id.eq(popupId)))
                .fetchFirst() != null;
    }

    @Override
    public Page<Reservation> findByUserIdAndStatus(Long userId, ReservationStatus status, Pageable pageable) {
        List<Reservation> reservations = queryFactory
                .selectFrom(reservation)
                .join(reservation.slot).fetchJoin()
                .join(reservation.slot.popup).fetchJoin()
                .where(reservation.user.id.eq(userId)
                        .and(reservation.reservationStatus.eq(status)))
                .orderBy(reservation.slot.slotDate.desc(), reservation.slot.slotTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(Wildcard.count)
                .from(reservation)
                .where(reservation.user.id.eq(userId)
                        .and(reservation.reservationStatus.eq(status)))
                .fetchOne();

        return new PageImpl<>(reservations, pageable, getTotal(totalCount));
    }

    @Override
    public Optional<Reservation> findReservationAndSlot(Long reservationId, Long userId) {
        Reservation findReservation = queryFactory
                .selectFrom(reservation)
                .join(reservation.slot).fetchJoin()
                .where(reservation.id.eq(reservationId)
                        .and(reservation.user.id.eq(userId)))
                .fetchOne();

        return Optional.ofNullable(findReservation);
    }

    @Override
    public Optional<Reservation> findReservationAndSlotPopUp(Long reservationId, Long userId) {
        Reservation findReservation = queryFactory
                .selectFrom(reservation)
                .join(reservation.slot).fetchJoin()
                .join(reservation.slot.popup).fetchJoin()
                .where(reservation.id.eq(reservationId)
                        .and(reservation.user.id.eq(userId)))
                .fetchOne();

        return Optional.ofNullable(findReservation);


    }

}
