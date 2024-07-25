package com.sparta.ezpzuser.domain.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.ezpzuser.domain.reservation.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationRepositoryCustom {
}
