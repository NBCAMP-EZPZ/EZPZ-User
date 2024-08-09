package com.sparta.ezpzuser.domain.slot.repository;

import com.sparta.ezpzuser.domain.slot.entity.Slot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlotRepository extends JpaRepository<Slot, Long>, SlotRepositoryCustom {

    Page<Slot> findAllByPopupId(Long popupId, Pageable pageable);

}
