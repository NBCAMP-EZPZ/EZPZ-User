package com.sparta.ezpzuser.domain.popup.repository.popup;

import com.sparta.ezpzuser.domain.popup.dto.PopupCondition;
import com.sparta.ezpzuser.domain.popup.entity.Popup;
import com.sparta.ezpzuser.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PopupRepositoryCustom {

    Page<Popup> findAllByPopupCondition(Pageable pageable, PopupCondition cond);

    Page<Popup> findAllLikedPopupByUser(User user, Pageable pageable);

    Popup findByReservationId(Long reservationId);

}
