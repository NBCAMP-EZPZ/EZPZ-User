package com.sparta.ezpzuser.domain.popup.dto;

import com.sparta.ezpzuser.domain.popup.entity.Popup;
import lombok.Getter;

@Getter
public class PopupPageResponseDto {

    private final Long popupId;
    private final String name;
    private final int likeCount;
    private final String popupStatus;
    private final String companyName;

    public PopupPageResponseDto(Popup popup) {
        this.popupId = popup.getId();
        this.name = popup.getName();
        this.likeCount = popup.getLikeCount();
        this.popupStatus = popup.getPopupStatus().toString();
        this.companyName = popup.getHost().getCompanyName();
    }

    public static PopupPageResponseDto of(Popup popup) {
        return new PopupPageResponseDto(popup);
    }
}
