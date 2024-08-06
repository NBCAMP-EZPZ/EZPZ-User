package com.sparta.ezpzuser.domain.reservation.service;

import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.common.lock.DistributedLock;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sparta.ezpzuser.common.exception.ErrorType.*;
import static com.sparta.ezpzuser.common.util.PageUtil.validatePageableWithPage;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SlotRepository slotRepository;

    /**
     * 예약 생성
     *
     * @param dto  예약 요청 DTO
     * @param user 로그인 사용자 정보
     * @return 생성된 예약 정보
     */
    @DistributedLock(key = "'createReservation-userId-'.concat(#user.id)")
    @CachePut(value = "reservation", key = "'reservation:' + #result.id")
    public ReservationResponseDto createReservation(ReservationRequestDto dto, User user) {
        Slot slot = getSlot(dto.getSlotId());
        validateDuplicateReservation(user, slot.getPopup());
        validateSlotStatus(slot);
        validateReservationCapacity(dto, slot);

        // 예약 생성
        Reservation reservation = reservationRepository.save(Reservation.of(dto.getNumberOfPersons(), user, slot));
        slot.increaseReservedCount(dto.getNumberOfPersons());
        slotRepository.save(slot);

        return ReservationResponseDto.of(reservation, slot);
    }

    /**
     * 예약 목록 조회
     *
     * @param pageable 페이징 정보
     * @param status   예약 상태
     * @param user     로그인 사용자 정보
     * @return 예약 목록
     */
    @Transactional(readOnly = true)
    public Page<ReservationResponseDto> findReservations(Pageable pageable, String status, User user) {
        Page<Reservation> page;
        if (status == null) {
            page = reservationRepository.findByUserId(user.getId(), pageable);
        } else {
            ReservationStatus reservationStatus = ReservationStatus.valueOf(status.toUpperCase());
            page = reservationRepository.findByUserIdAndStatus(user.getId(), reservationStatus, pageable);
        }
        validatePageableWithPage(pageable, page);
        return page.map(reservation -> ReservationResponseDto.of(reservation, reservation.getSlot()));
    }

    /**
     * 예약 상세 조회
     *
     * @param reservationId 예약 ID
     * @param user          로그인 사용자 정보
     * @return 예약 정보
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "reservation", key = "'reservation:' + #reservationId")
    public ReservationResponseDto findReservation(Long reservationId, User user) {
        Reservation reservation = getReservationAndSlotPopup(reservationId, user);
        return ReservationResponseDto.of(reservation, reservation.getSlot());
    }

    /**
     * 예약 취소
     *
     * @param reservationId 예약 ID
     * @param user          로그인 사용자 정보
     */
    @DistributedLock(key = "'cancelReservation-reservationId-'.concat(#reservationId)")
    @CacheEvict(value = "reservation", key = "'reservation:' + #reservationId")
    public void cancelReservation(Long reservationId, User user) {
        Reservation reservation = getReservationAndSlot(reservationId, user);
        reservation.cancel();

        Slot slot = reservation.getSlot();
        slot.decreaseReservedCount(reservation.getNumberOfPersons());
        slotRepository.save(slot);
    }

    /* UTIL */

    /**
     * 슬롯 조회
     *
     * @param slotId 슬롯 ID
     * @return 슬롯 정보
     */
    private Slot getSlot(Long slotId) {
        return slotRepository.findSlotByIdWithPopup(slotId)
                .orElseThrow(() -> new CustomException(SLOT_NOT_FOUND));
    }

    /**
     * 슬롯 예약 가능 여부 확인
     *
     * @param slot 예약 슬롯 정보
     */
    private void validateSlotStatus(Slot slot) {
        // 아직 진행 전인 슬롯만 예약 가능
        if (!slot.getSlotStatus().equals(SlotStatus.READY)) {
            throw new CustomException(SLOT_RESERVATION_CLOSED);
        }
    }

    /**
     * 예약 가능 인원 확인
     *
     * @param dto  예약 요청 DTO
     * @param slot 예약 슬롯 정보
     */
    private void validateReservationCapacity(ReservationRequestDto dto, Slot slot) {
        if (slot.getReservedCount() + dto.getNumberOfPersons() > slot.getTotalCount()
                || slot.getAvailableCount() < dto.getNumberOfPersons()) {
            throw new CustomException(RESERVATION_EXCEEDS_AVAILABLE_SLOTS);
        }
    }

    /**
     * 이미 예약한 내역 있는지 확인
     *
     * @param user  로그인 사용자 정보
     * @param popup 팝업 정보
     */
    private void validateDuplicateReservation(User user, Popup popup) {
        if (reservationRepository.existsByUserIdAndPopupId(user.getId(), popup.getId())) {
            throw new CustomException(RESERVATION_ALREADY_EXISTS);
        }
    }

    /**
     * 예약 조회 (팝업 정보 제외)
     *
     * @param reservationId 예약 ID
     * @param user          로그인 사용자 정보
     * @return 예약 정보
     */
    private Reservation getReservationAndSlot(Long reservationId, User user) {
        return reservationRepository.findReservationAndSlot(reservationId, user.getId())
                .orElseThrow(() -> new CustomException(RESERVATION_NOT_FOUND));
    }

    /**
     * 예약 조회 (팝업 정보 포함)
     *
     * @param reservationId 예약 ID
     * @param user          로그인 사용자 정보
     * @return 예약 정보
     */
    private Reservation getReservationAndSlotPopup(Long reservationId, User user) {
        return reservationRepository.findReservationAndSlotPopUp(reservationId, user.getId())
                .orElseThrow(() -> new CustomException(RESERVATION_NOT_FOUND));
    }

}
