package com.sparta.ezpzuser.domain.reservation.repository;

import com.sparta.ezpzuser.domain.reservation.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationRepositoryCustom {

    Page<Reservation> findByUserId(Long userId, Pageable pageable);

}
