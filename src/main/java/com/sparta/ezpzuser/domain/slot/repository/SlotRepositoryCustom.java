package com.sparta.ezpzuser.domain.slot.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sparta.ezpzuser.domain.slot.entity.Slot;

public interface SlotRepositoryCustom {
	Optional<Slot> findSlotByIdWithPopup(Long slotId);
	
	Page<Slot> findByPopupId(Long popupId, Pageable pageable);
}
