package com.sparta.ezpzuser.domain.popup.repository.popup;

import com.sparta.ezpzuser.domain.like.dto.LikePopupPageResponseDto;
import com.sparta.ezpzuser.domain.popup.dto.PopupCondition;
import com.sparta.ezpzuser.domain.popup.entity.Popup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PopupRepositoryCustom {
    Page<Popup> findAllPopupsByStatus(Pageable pageable, PopupCondition cond);

    Page<Popup> findPopupByIdList(Pageable pageable, List<Long> popupIdList);

    Popup findByReservationId(Long reservationId);
}
