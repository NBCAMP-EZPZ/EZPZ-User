package com.sparta.ezpzuser.domain.slot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sparta.ezpzuser.domain.slot.entity.Slot;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {
	@Query("SELECT s FROM Slot s JOIN FETCH s.popup WHERE s.id = :slotId")
	Slot findSlotByIdWithPopup(@Param("slotId") Long slotId);
	
	Page<Slot> findByPopupId(Long popupId, Pageable pageable);
	
}
