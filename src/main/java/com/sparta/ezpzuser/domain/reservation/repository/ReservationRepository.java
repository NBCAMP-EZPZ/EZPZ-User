package com.sparta.ezpzuser.domain.reservation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sparta.ezpzuser.domain.reservation.entity.Reservation;
import com.sparta.ezpzuser.domain.reservation.enums.ReservationStatus;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	
	@Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reservation r WHERE r.user.id = :userId AND r.slot.popup.id = :popupId")
	boolean existsByUserIdAndPopupId(@Param("userId") Long userId, @Param("popupId") Long popupId);
	
	@Query("SELECT r FROM Reservation r "
		+ "JOIN FETCH r.slot s "
		+ "JOIN FETCH s.popup p "
		+ "WHERE r.user.id = :userId "
		+ "AND r.reservationStatus = :status "
		+ "ORDER BY s.slotDate DESC, s.slotTime DESC")
	Page<Reservation> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") ReservationStatus status, Pageable pageable);
}
