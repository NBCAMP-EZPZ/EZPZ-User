package com.sparta.ezpzuser.domain.reservation.dto;

import com.sparta.ezpzuser.domain.reservation.entity.Reservation;
import com.sparta.ezpzuser.domain.slot.entity.Slot;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationResponseDto {
	private Long id;
	private String name;
	private String slotDate;
	private String slotTime;
	private String reservationStatus;
	
	private ReservationResponseDto(Reservation reservation, Slot slot) {
		this.id = reservation.getId();
		this.name = slot.getPopup().getName();
		this.slotDate = slot.getSlotDate().toString();
		this.slotTime = slot.getSlotTime().toString();
		this.reservationStatus = reservation.getReservationStatus().toString();
	}
	
	public static ReservationResponseDto of(Reservation reservation, Slot slot) {
		return new ReservationResponseDto(reservation, slot);
	}
}
