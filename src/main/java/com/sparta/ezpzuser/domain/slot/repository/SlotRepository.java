package com.sparta.ezpzuser.domain.slot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.ezpzuser.domain.slot.entity.Slot;

public interface SlotRepository extends JpaRepository<Slot, Long>, SlotRepositoryCustom {
}
