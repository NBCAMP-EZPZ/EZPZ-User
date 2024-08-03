package com.sparta.ezpzuser.domain.slot.repository;

import com.sparta.ezpzuser.domain.slot.entity.Slot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SlotRepositoryCustom {

    Optional<Slot> findSlotByIdWithPopup(Long slotId);

    Page<Slot> findByPopupId(Long popupId, Pageable pageable);

}
