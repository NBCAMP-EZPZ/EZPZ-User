package com.sparta.ezpzuser.domain.review.repository;

import com.sparta.ezpzuser.domain.reservation.entity.Reservation;

public interface ReviewRepositoryCustom {

    boolean existsByReservation(Reservation reservation);

}
