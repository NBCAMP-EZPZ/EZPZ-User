package com.sparta.ezpzuser.domain.item.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ezpzuser.domain.item.dto.ItemCondition;
import com.sparta.ezpzuser.domain.item.entity.Item;
import com.sparta.ezpzuser.domain.item.enums.ItemStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Objects;

import static com.sparta.ezpzuser.domain.item.entity.QItem.item;

@RequiredArgsConstructor
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Item> findAllItemsByHostAndPopupAndStatus(Pageable pageable, ItemCondition cond) {
        // 데이터 조회 쿼리
        List<Item> items = jpaQueryFactory
                .select(item)
                .from(item)
                .where(
                        hostIdEq(cond.getHostId()),
                        popupIdEq(cond.getPopupId()),
                        itemStatusEq(cond.getItemStatus())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(item.createdAt.desc())
                .fetch();

        // 카운트 쿼리
        Long totalSize = jpaQueryFactory
                .select(Wildcard.count)
                .from(item)
                .where(
                        hostIdEq(cond.getHostId()),
                        popupIdEq(cond.getPopupId()),
                        itemStatusEq(cond.getItemStatus())
                )
                .fetchOne();

        return PageableExecutionUtils.getPage(items, pageable, () -> totalSize);
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
                item.itemStatus.eq(ItemStatus.valueOf(itemStatus.toUpperCase())) : item.itemStatus.eq(ItemStatus.SALE);
    }
}
