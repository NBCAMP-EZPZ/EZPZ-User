package com.sparta.ezpzuser.domain.slot.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ezpzuser.domain.slot.entity.Slot;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.sparta.ezpzuser.domain.slot.entity.QSlot.slot;

@RequiredArgsConstructor
public class SlotRepositoryCustomImpl implements SlotRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Slot> findSlotByIdWithPopup(Long slotId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(slot)
                .join(slot.popup).fetchJoin()
                .where(slot.id.eq(slotId))
                .fetchOne()
        );
    }

}