package com.sparta.ezpzuser.domain.slot.controller;

import static com.sparta.ezpzuser.common.util.ControllerUtil.getResponseEntity;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.ezpzuser.common.dto.CommonResponse;
import com.sparta.ezpzuser.domain.slot.dto.SlotResponseDto;
import com.sparta.ezpzuser.domain.slot.service.SlotService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/popups/{popupId}/slots")
public class SlotController {
	private final SlotService slotService;
	
	@GetMapping
	public ResponseEntity<CommonResponse<?>> findSlotsByPopupId(
		@PathVariable Long popupId,
		@RequestParam(defaultValue = "1") int page) {
		
		Page<SlotResponseDto> responseDto = slotService.findSlotsByPopupId(page, popupId);
		
		return getResponseEntity(responseDto, "예약 가능 목록 조회 성공");
		
	}
}
