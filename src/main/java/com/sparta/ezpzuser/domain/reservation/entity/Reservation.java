package com.sparta.ezpzuser.domain.reservation.entity;

import com.sparta.ezpzuser.common.entity.Timestamped;
import com.sparta.ezpzuser.domain.reservation.enums.ReservationStatus;
import com.sparta.ezpzuser.domain.slot.entity.Slot;
import com.sparta.ezpzuser.domain.user.entity.User;

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
@Table(name = "reservation", indexes = {
	@Index(name = "idx_reservation_user_status", columnList = "user_id, reservation_status, slot_id")
})
public class Reservation extends Timestamped {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reservation_id")
	private Long id;
	
	@Column(nullable = false)
	private int numberOfPersons;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ReservationStatus reservationStatus;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "slot_id", nullable = false)
	private Slot slot;
	
	private Reservation(int numberOfPersons, User user, Slot slot) {
		this.numberOfPersons = numberOfPersons;
		this.reservationStatus = ReservationStatus.READY;
		this.user = user;
		this.slot = slot;
	}
	
	public static Reservation of(int numberOfPersons, User user, Slot slot) {
		return new Reservation(numberOfPersons, user, slot);
	}
	
	public void cancel() {
		this.reservationStatus = ReservationStatus.CANCEL;
	}
}
