package com.sparta.ezpzuser.domain.popup.repository.popup;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ezpzuser.domain.like.entity.LikeContentType;
import com.sparta.ezpzuser.domain.popup.dto.PopupCondition;
import com.sparta.ezpzuser.domain.popup.entity.Popup;
import com.sparta.ezpzuser.domain.popup.enums.ApprovalStatus;
import com.sparta.ezpzuser.domain.popup.enums.PopupStatus;
import com.sparta.ezpzuser.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Objects;

import static com.sparta.ezpzuser.common.util.RepositoryUtil.getTotal;
import static com.sparta.ezpzuser.domain.like.entity.QLike.like;
import static com.sparta.ezpzuser.domain.popup.entity.QPopup.popup;
import static com.sparta.ezpzuser.domain.reservation.entity.QReservation.reservation;
import static com.sparta.ezpzuser.domain.slot.entity.QSlot.slot;

@RequiredArgsConstructor
public class PopupRepositoryCustomImpl implements PopupRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Popup> findAllByPopupCondition(Pageable pageable, PopupCondition cond) {
        List<Popup> popups = queryFactory
                .selectFrom(popup)
                .join(popup.host).fetchJoin()
                .where(
                        popup.approvalStatus.eq(ApprovalStatus.APPROVED),
                        popupStatusEq(cond.getPopupStatus())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(popup.createdAt.desc())
                .fetch();

        Long totalCount = queryFactory.select(Wildcard.count)
                .from(popup)
                .where(
                        popup.approvalStatus.eq(ApprovalStatus.APPROVED),
                        popupStatusEq(cond.getPopupStatus())
                )
                .fetchOne();

        return PageableExecutionUtils.getPage(popups, pageable, () -> getTotal(totalCount));
    }

    @Override
    public Page<Popup> findAllLikedPopupByUser(User user, Pageable pageable) {
        List<Popup> popups = queryFactory
                .selectFrom(popup)
                .join(popup.host).fetchJoin()
                .join(like).fetchJoin()
                .on(
                        like.contentType.eq(LikeContentType.POPUP),
                        like.contentId.eq(popup.id)
                )
                .where(
                        like.user.eq(user),
                        popup.popupStatus.ne(PopupStatus.CANCELED),
                        popup.approvalStatus.eq(ApprovalStatus.APPROVED)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(like.likedAt.desc())
                .fetch();

        Long totalCount = queryFactory
                .select(Wildcard.count)
                .from(popup)
                .join(like).fetchJoin()
                .on(
                        like.contentType.eq(LikeContentType.POPUP),
                        like.contentId.eq(popup.id)
                )
                .where(
                        like.user.eq(user),
                        popup.popupStatus.ne(PopupStatus.CANCELED),
                        popup.approvalStatus.eq(ApprovalStatus.APPROVED)
                )
                .fetchOne();

        return PageableExecutionUtils.getPage(popups, pageable, () -> getTotal(totalCount));
    }

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

    // 조건 : 팝업 ID
    private BooleanExpression popupStatusEq(String statusBy) {
        return Objects.nonNull(statusBy) && !"all".equals(statusBy) ?
                popup.popupStatus.eq(PopupStatus.valueOf(statusBy.toUpperCase())) : null;
    }

}
