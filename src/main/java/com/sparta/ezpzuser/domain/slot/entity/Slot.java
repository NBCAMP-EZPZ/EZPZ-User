package com.sparta.ezpzuser.domain.slot.entity;

import com.sparta.ezpzuser.domain.popup.entity.Popup;
import com.sparta.ezpzuser.domain.slot.enums.SlotStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

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

    public Slot(LocalDate slotDate, LocalTime slotTime, int totalCount, Popup popup, SlotStatus slotStatus) {
        this.slotDate = slotDate;
        this.slotTime = slotTime;
        this.availableCount = totalCount;
        this.totalCount = totalCount;
        this.reservedCount = 0;
        this.slotStatus = slotStatus;
        this.popup = popup;
    }

    public static Slot of(LocalDate slotDate, LocalTime slotTime, int totalCount, Popup popup, SlotStatus slotStatus) {
        return new Slot(slotDate, slotTime, totalCount, popup, slotStatus);
    }

    public void increaseReservedCount(int numberOfPersons) {
        this.reservedCount += numberOfPersons;
    }

    public void decreaseReservedCount(int numberOfPersons) {
        this.reservedCount -= numberOfPersons;
    }

}
