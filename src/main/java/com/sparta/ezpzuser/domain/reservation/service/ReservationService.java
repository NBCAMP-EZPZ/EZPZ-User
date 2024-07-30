package com.sparta.ezpzuser.domain.reservation.service;

import static com.sparta.ezpzuser.common.util.PageUtil.validatePageableWithPage;

import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.common.exception.ErrorType;
import com.sparta.ezpzuser.domain.popup.entity.Popup;
import com.sparta.ezpzuser.domain.reservation.dto.ReservationRequestDto;
import com.sparta.ezpzuser.domain.reservation.dto.ReservationResponseDto;
import com.sparta.ezpzuser.domain.reservation.entity.Reservation;
import com.sparta.ezpzuser.domain.reservation.enums.ReservationStatus;
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
	@CachePut(value = "reservation", key = "'reservation:' + #result.id")
	public ReservationResponseDto createReservation(ReservationRequestDto requestDto, User user) {
		Slot slot = getSlotByIdWithPopup(requestDto.getSlotId());
		
		existReservation(user, slot.getPopup());
		validateSlotStatus(slot);
		validateReservationCapacity(requestDto, slot);
		
		// 예약 생성
		Reservation saveReservation = reservationRepository.save(Reservation.of(requestDto.getNumberOfPersons(), user, slot));
		slot.increaseReservedCount(requestDto.getNumberOfPersons());
		slotRepository.save(slot);
		
		return ReservationResponseDto.of(saveReservation, slot);
	}
	
	/**
	 * 예약 목록 조회
	 *
	 * @param pageable 페이징 정보
	 * @param status 예약 상태
	 * @param user 로그인 사용자 정보
	 * @return 예약 목록
	 */
	public Page<ReservationResponseDto> findReservations(Pageable pageable, String status, User user) {
		ReservationStatus reservationStatus = ReservationStatus.valueOf(status.toUpperCase());
		Page<Reservation> reservationPage = reservationRepository.findByUserIdAndStatus(user.getId(), reservationStatus, pageable);
		validatePageableWithPage(pageable, reservationPage);
		
		return reservationPage.map(r -> ReservationResponseDto.of(r, r.getSlot()));
	}
	
	/**
	 * 예약 상세 조회
	 *
	 * @param reservationId 예약 ID
	 * @param user 로그인 사용자 정보
	 * @return 예약 정보
	 */
	@Cacheable(value = "reservation", key = "'reservation:' + #reservationId")
	public ReservationResponseDto findReservation(Long reservationId, User user) {
		Reservation reservation = getReservationAndSlotPopup(reservationId, user);
		
		return ReservationResponseDto.of(reservation, reservation.getSlot());
	}
	
	
	
	/**
	 * 예약 취소
	 *
	 * @param reservationId 예약 ID
	 * @param user 로그인 사용자 정보
	 */
	@Transactional
	@CacheEvict(value = "reservation", key = "'reservation:' + #reservationId")
	public void cancelReservation(Long reservationId, User user) {
		Reservation reservation = getReservationAndSlot(reservationId, user);
		
		Slot slot = reservation.getSlot();
		slot.decreaseReservedCount(reservation.getNumberOfPersons());
		slotRepository.save(slot);
		
		reservation.cancel();
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
		if (slot.getReservedCount() + requestDto.getNumberOfPersons() > slot.getTotalCount()
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
	
	/**
	 * 예약 조회 (팝업 정보 제외)
	 *
	 * @param reservationId 예약 ID
	 * @param user 로그인 사용자 정보
	 * @return 예약 정보
	 */
	private Reservation getReservationAndSlot(Long reservationId, User user) {
		return reservationRepository.findReservationAndSlot(reservationId, user.getId())
			.orElseThrow(() -> new CustomException(ErrorType.RESERVATION_NOT_FOUND));
	}
	
	/**
	 * 예약 조회 (팝업 정보 포함)
	 *
	 * @param reservationId 예약 ID
	 * @param user 로그인 사용자 정보
	 * @return 예약 정보
	 */
	private Reservation getReservationAndSlotPopup(Long reservationId, User user) {
		return reservationRepository.findReservationAndSlotPopUp(reservationId, user.getId())
			.orElseThrow(() -> new CustomException(ErrorType.RESERVATION_NOT_FOUND));
	}
}
