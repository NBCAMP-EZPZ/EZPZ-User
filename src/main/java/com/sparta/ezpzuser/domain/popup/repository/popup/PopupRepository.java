package com.sparta.ezpzuser.domain.popup.repository.popup;

import com.sparta.ezpzuser.domain.popup.entity.Popup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PopupRepository extends JpaRepository<Popup, Long>, PopupRepositoryCustom {
}
