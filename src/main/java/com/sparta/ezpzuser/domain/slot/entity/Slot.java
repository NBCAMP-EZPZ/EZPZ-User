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

    private int availableCount;

    private int totalCount;

    private int reservedCount;

    @Enumerated(value = EnumType.STRING)
    private SlotStatus slotStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "popup_id")
    private Popup popup;

    private Slot(int availableCount, int totalCount, int reservedCount, SlotStatus slotStatus, Popup popup) {
        this.slotDate = LocalDate.now();
        this.slotTime = LocalTime.now();
        this.availableCount = availableCount;
        this.reservedCount = reservedCount;
        this.totalCount = totalCount;
        this.slotStatus = slotStatus;
        this.popup = popup;
    }

    public static Slot createMockSlot(int availableCount, int totalCount, int reservedCount, Popup popup) {
        return new Slot(availableCount, totalCount, reservedCount, SlotStatus.PROCEEDING, popup);
    }

    public static Slot createMockSlot(int availableCount, int totalCount, Popup popup) {
        return new Slot(availableCount, totalCount, 0, SlotStatus.PROCEEDING, popup);
    }

    public static Slot createMockFinishedSlot(int availableCount, int totalCount, Popup popup) {
        return new Slot(availableCount, totalCount, 0, SlotStatus.FINISHED, popup);
    }

    /**
     * 예약 가능 여부 검증
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

    /**
     * 예약 개수 증가
     *
     * @param numberOfPersons 예약 인원
     */
    public void increaseReservedCount(int numberOfPersons) {
        if (reservedCount + numberOfPersons > this.totalCount) {
            throw new CustomException(EXCEED_TOTAL_COUNT);
        }
        this.reservedCount += numberOfPersons;
    }

    /**
     * 예약 개수 감소
     *
     * @param numberOfPersons 예약 취소 인원
     */
    public void decreaseReservedCount(int numberOfPersons) {
        if (reservedCount < numberOfPersons) {
            throw new CustomException(RESERVED_COUNT_DEFICIENT);
        }
        this.reservedCount -= numberOfPersons;
    }

}
