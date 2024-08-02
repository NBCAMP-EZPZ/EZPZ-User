package com.sparta.ezpzuser.domain.slot.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import com.sparta.ezpzuser.domain.popup.entity.Popup;
import com.sparta.ezpzuser.domain.slot.enums.SlotStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
	
	@Column(nullable = false)
	private LocalDate slotDate;
	
	@Column(nullable = false)
	private LocalTime slotTime;
	
	@Column(nullable = false)
	private int availableCount;
	
	@Column(nullable = false)
	private int totalCount;
	
	@Column(nullable = false)
	private int reservedCount;
	
	@Enumerated(value = EnumType.STRING)
	@Column(nullable = false)
	private SlotStatus slotStatus;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "popup_id", nullable = false)
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
