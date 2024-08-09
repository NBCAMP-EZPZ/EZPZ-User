package com.sparta.ezpzuser.domain.slot.dto;

import com.sparta.ezpzuser.domain.slot.entity.Slot;
import com.sparta.ezpzuser.domain.slot.enums.SlotStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SlotResponseDto {

    private Long slotId;
    private SlotStatus slotStatus;
    private String slotDate;
    private String slotTime;

    private SlotResponseDto(Slot slot) {
        this.slotId = slot.getId();
        this.slotDate = slot.getSlotDate().toString();
        this.slotTime = slot.getSlotTime().toString();
        this.slotStatus = slot.getSlotStatus();
    }

    public static SlotResponseDto of(Slot slot) {
        return new SlotResponseDto(slot);
    }

}
