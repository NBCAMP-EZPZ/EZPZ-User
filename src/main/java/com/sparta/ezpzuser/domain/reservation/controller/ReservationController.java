package com.sparta.ezpzuser.domain.reservation.controller;

import static com.sparta.ezpzuser.common.util.ControllerUtil.getResponseEntity;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.ezpzuser.common.dto.CommonResponse;
import com.sparta.ezpzuser.domain.reservation.dto.ReservationRequestDto;
import com.sparta.ezpzuser.domain.reservation.dto.ReservationResponseDto;
import com.sparta.ezpzuser.domain.reservation.service.ReservationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservations")
public class ReservationController {
	private final ReservationService reservationService;
	
	/**
	 * 예약 등록
	 *
	 * @param requestDto 예약 요청 DTO
	 * @param userDetails 로그인 사용자 정보
	 * @return 생성된 예약 정보
	 */
	@PostMapping
	public ResponseEntity<CommonResponse<?>> createReservation(
		@Valid @RequestBody ReservationRequestDto requestDto
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		
		ReservationResponseDto responseDto = reservationService.createReservation(requestDto, userDetails.getUser());
		
		return getResponseEntity(responseDto, "예약 등록 성공");
	}
	
}
