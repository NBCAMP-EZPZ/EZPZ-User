package com.sparta.ezpzuser.domain.slot.dto;

import com.sparta.ezpzuser.domain.slot.entity.Slot;

import lombok.Getter;

@Getter
public class SlotResponseDto {
	private Long id;
	private String slotDate;
	private String slotTime;
	private String slotStatus;
	
	private SlotResponseDto(Slot slot) {
		this.id = slot.getId();
		this.slotDate = slot.getSlotDate().toString();
		this.slotTime = slot.getSlotTime().toString();
		this.slotStatus = slot.getSlotStatus().toString();
	}
	
	public static SlotResponseDto of(Slot slot) {
		return new SlotResponseDto(slot);
	}
}
