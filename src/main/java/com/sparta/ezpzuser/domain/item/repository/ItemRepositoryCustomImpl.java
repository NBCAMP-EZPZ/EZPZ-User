package com.sparta.ezpzuser.domain.item.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ezpzuser.domain.item.dto.ItemCondition;
import com.sparta.ezpzuser.domain.item.entity.Item;
import com.sparta.ezpzuser.domain.item.enums.ItemStatus;
import com.sparta.ezpzuser.domain.like.entity.LikeContentType;
import com.sparta.ezpzuser.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Objects;

import static com.sparta.ezpzuser.common.util.RepositoryUtil.getTotal;
import static com.sparta.ezpzuser.domain.item.entity.QItem.item;
import static com.sparta.ezpzuser.domain.like.entity.QLike.like;

@RequiredArgsConstructor
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Item> findAllByItemCondition(Pageable pageable, ItemCondition cond) {
        List<Item> items = queryFactory
                .selectFrom(item)
                .where(
                        popupIdEq(cond.getPopupId()),
                        itemStatusEq(cond.getItemStatus())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(item.createdAt.desc())
                .fetch();

        Long totalCount = queryFactory
                .select(Wildcard.count)
                .from(item)
                .where(
                        popupIdEq(cond.getPopupId()),
                        itemStatusEq(cond.getItemStatus())
                )
                .fetchOne();

        return PageableExecutionUtils.getPage(items, pageable, () -> getTotal(totalCount));
    }

    @Override
    public Page<Item> findAllLikedItemByUser(User user, Pageable pageable) {
        List<Item> items = queryFactory
                .selectFrom(item)
                .join(like).fetchJoin()
                .on(
                        like.contentType.eq(LikeContentType.ITEM),
                        like.contentId.eq(item.id)
                )
                .where(
                        like.user.eq(user),
                        item.itemStatus.ne(ItemStatus.SALE_END)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(like.likedAt.desc())
                .fetch();

        Long totalCount = queryFactory
                .select(Wildcard.count)
                .from(item)
                .join(like).fetchJoin()
                .on(
                        like.contentType.eq(LikeContentType.ITEM),
                        like.contentId.eq(item.id)
                )
                .where(
                        like.user.eq(user)
                )
                .fetchOne();

        return PageableExecutionUtils.getPage(items, pageable, () -> getTotal(totalCount));
    }

    // 조건 : 호스트 ID
    private BooleanExpression hostIdEq(String hostId) {
        return Objects.nonNull(hostId) && !"all".equals(hostId) ?
                item.popup.host.id.eq(Long.valueOf(hostId)) : null;
    }

    // 조건 : 팝업 ID
    private BooleanExpression popupIdEq(String popupId) {
        return Objects.nonNull(popupId) && !"all".equals(popupId) ?
                item.popup.id.eq(Long.valueOf(popupId)) : null;
    }

    // 조건 : 상품 상태
    private BooleanExpression itemStatusEq(String itemStatus) {
        return Objects.nonNull(itemStatus) && !"all".equals(itemStatus) ?
                item.itemStatus.eq(ItemStatus.valueOf(itemStatus.toUpperCase())) : null;
    }

}
