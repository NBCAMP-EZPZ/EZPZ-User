package com.sparta.ezpzuser.domain.slot.service;

import static com.sparta.ezpzuser.common.util.PageUtil.validatePageableWithPage;

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
	
	/**
	 * 팝업 ID로 슬롯 목록 조회
	 *
	 * @param pageable 페이징 정보
	 * @param popupId 팝업 ID
	 * @return 슬롯 목록
	 */
	public Page<SlotResponseDto> findSlotsByPopupId(Pageable pageable, Long popupId) {
		
		Page<Slot> slotPage = slotRepository.findByPopupId(popupId, pageable);
		validatePageableWithPage(pageable, slotPage);
		
		return slotPage.map(SlotResponseDto::of);
	}
}
