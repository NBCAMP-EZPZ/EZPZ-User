package com.sparta.ezpzuser.domain.reservation.controller;

import static com.sparta.ezpzuser.common.util.ControllerUtil.getResponseEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.ezpzuser.common.dto.CommonResponse;
import com.sparta.ezpzuser.common.security.UserDetailsImpl;
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
		@Valid @RequestBody ReservationRequestDto requestDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		
		ReservationResponseDto responseDto = reservationService.createReservation(requestDto, userDetails.getUser());
		
		return getResponseEntity(responseDto, "예약 등록 성공");
	}
	
	/**
	 * 예약 목록 조회
	 *
	 * @param pageable 페이징 정보
	 * @param status 예약 상태
	 * @param userDetails 로그인 사용자 정보
	 * @return 예약 목록
	 */
	@GetMapping
	public ResponseEntity<CommonResponse<?>> findReservations (
		Pageable pageable,
		@RequestParam(defaultValue = "READY") String status,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		
		Page<ReservationResponseDto> responsePage = reservationService.findReservations(pageable, status, userDetails.getUser());
		
		return getResponseEntity(responsePage, "예약 목록 조회 성공");
	}
	
	/**
	 * 예약 상세 조회
	 *
	 * @param reservationId 예약 ID
	 * @param userDetails 로그인 사용자 정보
	 * @return 예약 상세 정보
	 */
	@GetMapping("/{reservationId}")
	public ResponseEntity<CommonResponse<?>> findReservation(
		@PathVariable Long reservationId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		
		ReservationResponseDto responseDto = reservationService.findReservation(reservationId, userDetails.getUser());
		
		return getResponseEntity(responseDto, "예약 조회 성공");
	}
	
	
	
	/**
	 * 예약 취소
	 *
	 * @param reservationId 예약 ID
	 * @param userDetails 로그인 사용자 정보
	 * @return 예약 취소 결과
	 */
	@PatchMapping("/{reservationId}")
	public ResponseEntity<CommonResponse<?>> cancelReservation(
		@PathVariable Long reservationId,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		
		reservationService.cancelReservation(reservationId, userDetails.getUser());
		
		return getResponseEntity("예약 취소 성공");
	}
	
}
