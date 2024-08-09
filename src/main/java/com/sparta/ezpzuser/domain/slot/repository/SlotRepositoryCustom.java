package com.sparta.ezpzuser.domain.slot.repository;

import com.sparta.ezpzuser.domain.slot.entity.Slot;

import java.util.Optional;

public interface SlotRepositoryCustom {

    Optional<Slot> findSlotByIdWithPopup(Long slotId);

}
