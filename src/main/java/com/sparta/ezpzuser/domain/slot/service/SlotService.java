package com.sparta.ezpzuser.domain.slot.service;

import static com.sparta.ezpzuser.common.util.PageUtil.checkValidatePage;
import static com.sparta.ezpzuser.common.util.PageUtil.createPageable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.ezpzuser.domain.slot.dto.SlotResponseDto;
import com.sparta.ezpzuser.domain.slot.entity.Slot;
import com.sparta.ezpzuser.domain.slot.repository.SlotRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SlotService {
	private final SlotRepository slotRepository;
	
	public Page<SlotResponseDto> findSlotsByPopupId(int page, Long popupId) {
		
		Pageable pageable = createPageable(page);
		Page<Slot> slotPage = slotRepository.findByPopupId(popupId, pageable);
		checkValidatePage(page, slotPage);
		
		return slotPage.map(SlotResponseDto::of);
	}
}
