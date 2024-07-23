package com.sparta.ezpzuser.domain.reservation.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.common.exception.ErrorType;
import com.sparta.ezpzuser.domain.popup.entity.Popup;
import com.sparta.ezpzuser.domain.reservation.dto.ReservationRequestDto;
import com.sparta.ezpzuser.domain.reservation.dto.ReservationResponseDto;
import com.sparta.ezpzuser.domain.reservation.entity.Reservation;
import com.sparta.ezpzuser.domain.reservation.repository.ReservationRepository;
import com.sparta.ezpzuser.domain.slot.entity.Slot;
import com.sparta.ezpzuser.domain.slot.enums.SlotStatus;
import com.sparta.ezpzuser.domain.slot.repository.SlotRepository;
import com.sparta.ezpzuser.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {
	private final ReservationRepository reservationRepository;
	private final SlotRepository slotRepository;
	
	/**
	 * 예약 생성
	 *
	 * @param requestDto 예약 요청 DTO
	 * @param user 로그인 사용자 정보
	 * @return 생성된 예약 정보
	 */
	@Transactional
	public ReservationResponseDto createReservation(ReservationRequestDto requestDto, User user) {
		Slot slot = getSlotByIdWithPopup(requestDto.getSlotId());
		
		existReservation(user, slot.getPopup());
		validateSlotStatus(slot);
		validateReservationCapacity(requestDto, slot);
		
		// 예약 생성
		Reservation saveReservation = reservationRepository.save(Reservation.of(requestDto.getNumberOfPersons(), user, slot));
		slot.decreaseTotalCount(requestDto.getNumberOfPersons());
		slotRepository.save(slot);
		
		return ReservationResponseDto.of(saveReservation, slot);
	}
	
	
	/* UTIL */
	
	/**
	 * 슬롯 조회
	 *
	 * @param slotId 슬롯 ID
	 * @return 슬롯 정보
	 */
	private Slot getSlotByIdWithPopup(Long slotId) {
		Optional<Slot> slot = slotRepository.findSlotByIdWithPopup(slotId);
		
		if (slot.isEmpty()) {
			throw new CustomException(ErrorType.SLOT_NOT_FOUND);
		}
		
		return slot.get();
	}
	
	
	/**
	 * 슬롯 예약 가능 여부 확인
	 *
	 * @param slot 예약 슬롯 정보
	 */
	private void validateSlotStatus(Slot slot) {
		if(slot.getSlotStatus().equals(SlotStatus.FINISHED)) {
			throw new CustomException(ErrorType.SLOT_RESERVATION_FINISHED);
		}
	}
	
	/**
	 * 예약 가능 인원 확인
	 *
	 * @param requestDto 예약 요청 DTO
	 * @param slot 예약 슬롯 정보
	 */
	private void validateReservationCapacity(ReservationRequestDto requestDto, Slot slot) {
		if (slot.getTotalCount() - requestDto.getNumberOfPersons() < 0
			|| slot.getAvailableCount() < requestDto.getNumberOfPersons()) {
			throw new CustomException(ErrorType.RESERVATION_EXCEEDS_AVAILABLE_SLOTS);
		}
	}
	
	/**
	 * 이미 예약한 내역 있는지 확인
	 *
	 * @param user 로그인 사용자 정보
	 * @param popup 팝업 정보
	 */
	private void existReservation(User user, Popup popup) {
		if (reservationRepository.existsByUserIdAndPopupId(user.getId(), popup.getId())) {
			throw new CustomException(ErrorType.RESERVATION_ALREADY_EXISTS);
		}
	}
}
