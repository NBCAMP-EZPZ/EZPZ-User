package com.sparta.ezpzuser.domain.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sparta.ezpzuser.domain.reservation.entity.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	
	@Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reservation r WHERE r.user.id = :userId AND r.slot.popup.id = :popupId")
	boolean existsByUserIdAndPopupId(@Param("userId") Long userId, @Param("popupId") Long popupId);
}
