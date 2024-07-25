package com.sparta.ezpzuser.domain.slot.repository;

import static com.sparta.ezpzuser.common.util.RepositoryUtil.getTotal;
import static com.sparta.ezpzuser.domain.slot.entity.QSlot.slot;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ezpzuser.domain.slot.entity.Slot;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SlotRepositoryCustomImpl implements SlotRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	
	@Override
	public Optional<Slot> findSlotByIdWithPopup(Long slotId) {
		Slot findSlot = queryFactory
			.selectFrom(slot)
			.join(slot.popup).fetchJoin()
			.where(slot.id.eq(slotId))
			.fetchOne();
		
		return Optional.ofNullable(findSlot);
	}
	
	@Override
	public Page<Slot> findByPopupId(Long popupId, Pageable pageable) {
		List<Slot> slots = queryFactory
			.selectFrom(slot)
			.where(slot.popup.id.eq(popupId))
			.orderBy(slot.id.asc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();
		
		Long totalCount = queryFactory
			.select(Wildcard.count)
			.from(slot)
			.where(slot.popup.id.eq(popupId))
			.fetchOne();
		
		return new PageImpl<>(slots, pageable, getTotal(totalCount));
	}
}
