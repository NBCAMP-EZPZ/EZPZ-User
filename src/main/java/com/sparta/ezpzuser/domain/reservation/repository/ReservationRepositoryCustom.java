package com.sparta.ezpzuser.domain.reservation.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sparta.ezpzuser.domain.reservation.entity.Reservation;
import com.sparta.ezpzuser.domain.reservation.enums.ReservationStatus;

public interface ReservationRepositoryCustom {
	boolean existsByUserIdAndPopupId(Long userId, Long popupId);
	
	Page<Reservation> findByUserIdAndStatus(Long userId, ReservationStatus status, Pageable pageable);
	
	Optional<Reservation> findReservationAndSlot(Long reservationId, Long userId);
	
	Optional<Reservation> findReservationAndSlotPopUp(Long reservationId, Long userId);
}
