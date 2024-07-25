package com.sparta.ezpzuser.domain.popup.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ezpzuser.domain.popup.entity.Popup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.sparta.ezpzuser.domain.popup.entity.QPopup.popup;
import static com.sparta.ezpzuser.domain.reservation.entity.QReservation.reservation;
import static com.sparta.ezpzuser.domain.slot.entity.QSlot.slot;

@Repository
@RequiredArgsConstructor
public class PopupRepositoryImpl implements PopupRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Popup findByReservationId(Long reservationId) {
        return queryFactory
                .select(popup)
                .from(reservation)
                .join(reservation.slot, slot).fetchJoin()
                .join(slot.popup, popup).fetchJoin()
                .where(reservation.id.eq(reservationId))
                .fetchOne();
    }

}
