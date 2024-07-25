package com.sparta.ezpzuser.domain.popup.repository;

import com.sparta.ezpzuser.domain.popup.entity.Popup;

public interface PopupRepositoryCustom {

    Popup findByReservationId(Long reservationId);

}
