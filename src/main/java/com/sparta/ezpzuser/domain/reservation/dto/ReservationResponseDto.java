package com.sparta.ezpzuser.domain.reservation.dto;

import com.sparta.ezpzuser.domain.reservation.entity.Reservation;
import com.sparta.ezpzuser.domain.reservation.enums.ReservationStatus;
import com.sparta.ezpzuser.domain.slot.entity.Slot;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationResponseDto {

    private Long reservationId;
    private ReservationStatus reservationStatus;
    private String popupName;
    private String slotDate;
    private String slotTime;

    private ReservationResponseDto(Reservation reservation, Slot slot) {
        this.reservationId = reservation.getId();
        this.popupName = slot.getPopup().getName();
        this.slotDate = slot.getSlotDate().toString();
        this.slotTime = slot.getSlotTime().toString();
        this.reservationStatus = reservation.getReservationStatus();
    }

    public static ReservationResponseDto of(Reservation reservation, Slot slot) {
        return new ReservationResponseDto(reservation, slot);
    }

}
