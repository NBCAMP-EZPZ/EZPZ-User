package com.sparta.ezpzuser.domain.popup.repository.popup;

import com.sparta.ezpzuser.common.util.PageUtil;
import com.sparta.ezpzuser.domain.popup.entity.Popup;
import org.springframework.data.domain.Page;

public interface PopupRepositoryCustom {
    Page<Popup> findAllPopupsByStatus(PageUtil pageUtil);
}
