package com.sparta.ezpzuser.domain.popup.repository.popup;

import com.sparta.ezpzuser.domain.popup.entity.Popup;
import com.sparta.ezpzuser.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PopupRepositoryCustom {

    Page<Popup> findAllByPopupStatus(String popupStatus, Pageable pageable);

    Page<Popup> findAllLikedPopupByUser(User user, Pageable pageable);

    Popup findByReservationId(Long reservationId);

}
