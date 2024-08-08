package com.sparta.ezpzuser.domain.slot.entity;

import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.domain.popup.entity.Popup;
import com.sparta.ezpzuser.domain.slot.enums.SlotStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

import static com.sparta.ezpzuser.common.exception.ErrorType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "slot", indexes = {
        @Index(name = "idx_popup_slot", columnList = "popup_id, slot_id")
})
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slot_id")
    private Long id;

    private LocalDate slotDate;

    private LocalTime slotTime;

    private int availableCount; // 한 팀당 예약 가능한 최대 인원 수

    private int totalCount; // 예약 가능한 총 팀 수

    private int reservedCount; // 예약된 팀 수

    @Enumerated(value = EnumType.STRING)
    private SlotStatus slotStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_id")
    private Popup popup;

    private Slot(int availableCount, int totalCount, Popup popup, SlotStatus slotStatus) {
        this.slotDate = LocalDate.now();
        this.slotTime = LocalTime.now();
        this.availableCount = availableCount;
        this.totalCount = totalCount;
        this.reservedCount = 0;
        this.slotStatus = slotStatus;
        this.popup = popup;
    }

    private Slot(int availableCount, int totalCount, int reservedCount, Popup popup) {
        this.slotDate = LocalDate.now();
        this.slotTime = LocalTime.now();
        this.availableCount = availableCount;
        this.totalCount = totalCount;
        this.reservedCount = reservedCount;
        this.slotStatus = SlotStatus.PROCEEDING;
        this.popup = popup;
    }

    public static Slot createMockSlot(int availableCount, int totalCount, Popup popup, SlotStatus slotStatus) {
        return new Slot(availableCount, totalCount, popup, slotStatus);
    }

    public static Slot createMockSlot(int availableCount, int totalCount, int reservedCount, Popup popup) {
        return new Slot(availableCount, totalCount, reservedCount, popup);
    }

    /**
     * 예약 가능한지 검증
     *
     * @param numberOfPersons 예약 인원 수
     */
    public void verifyReservationAvailability(int numberOfPersons) {
        // 예약 진행 중인 슬롯인지 확인
        if (!this.slotStatus.equals(SlotStatus.PROCEEDING)) {
            throw new CustomException(SLOT_RESERVATION_CLOSED);
        }
        // 예약이 다 찼는지 확인
        if (this.reservedCount == this.totalCount) {
            throw new CustomException(SLOT_FULL);
        }
        // 예약 가능한 인원 수인지 확인
        if (this.reservedCount + numberOfPersons > this.totalCount
                || numberOfPersons > this.availableCount) {
            throw new CustomException(RESERVATION_EXCEEDS_AVAILABLE_SLOTS);
        }
    }

    public void increaseReservedCount(int numberOfPersons) {
        this.reservedCount += numberOfPersons;
    }

    public void decreaseReservedCount(int numberOfPersons) {
        this.reservedCount -= numberOfPersons;
    }

}
