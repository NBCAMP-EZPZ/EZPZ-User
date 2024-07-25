package com.sparta.ezpzuser.domain.reservation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReservationRequestDto {
	@NotNull(message = "예약할 시간을 선택해 주세요")
	private Long slotId;
	
	@NotNull(message = "예약 인원을 선택해 주세요")
	private int numberOfPersons;
}
