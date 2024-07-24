package com.sparta.ezpzuser.domain.popup.repository.popup;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ezpzuser.common.util.PageUtil;
import com.sparta.ezpzuser.domain.popup.entity.Popup;
import com.sparta.ezpzuser.domain.popup.enums.ApprovalStatus;
import com.sparta.ezpzuser.domain.popup.enums.PopupStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Objects;

import static com.sparta.ezpzuser.domain.popup.entity.QPopup.popup;

@RequiredArgsConstructor
public class PopupRepositoryCustomImpl implements PopupRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Popup> findAllPopupsByStatus(PageUtil pageUtil) {
        JPAQuery<Popup> query = findAllPopupsByStatusQuery(popup, pageUtil)
                .offset(pageUtil.toPageable().getOffset())
                .limit(pageUtil.toPageable().getPageSize())
                .orderBy(popup.createdAt.desc());

        List<Popup> popups = query.fetch();
        Long totalSize = countQuery(pageUtil).fetch().get(0);

        return PageableExecutionUtils.getPage(popups, pageUtil.toPageable(), () -> totalSize);
    }

    private <T> JPAQuery<T> findAllPopupsByStatusQuery(Expression<T> expr, PageUtil pageUtil) {
        return jpaQueryFactory.select(expr)
                .from(popup)
                .where(
                        popup.approvalStatus.eq(ApprovalStatus.APPROVED),
                        popupStatusEq(pageUtil.getFirstStatus())
                );
    }

    private JPAQuery<Long> countQuery(PageUtil pageUtil) {
        return jpaQueryFactory.select(Wildcard.count)
                .from(popup)
                .where(
                        popup.approvalStatus.eq(ApprovalStatus.APPROVED),
                        popupStatusEq(pageUtil.getFirstStatus())
                );
    }

    private BooleanExpression popupStatusEq(String statusBy) {
        return Objects.nonNull(statusBy) && !"all".equals(statusBy) ?
                popup.popupStatus.eq(PopupStatus.valueOf(statusBy.toUpperCase())) : null;
    }
}
