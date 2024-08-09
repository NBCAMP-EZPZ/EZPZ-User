package com.sparta.ezpzuser.domain.popup.dto;

import com.sparta.ezpzuser.domain.popup.entity.Popup;
import com.sparta.ezpzuser.domain.popup.enums.PopupStatus;
import lombok.Getter;

@Getter
public class PopupPageResponseDto {

    private final Long popupId;
    private final PopupStatus popupStatus;
    private final String popupName;
    private final int likeCount;
    private final String companyName;
    private final String thumbnailUrl;

    public PopupPageResponseDto(Popup popup) {
        this.popupId = popup.getId();
        this.popupName = popup.getName();
        this.likeCount = popup.getLikeCount();
        this.thumbnailUrl = popup.getThumbnailUrl();
        this.popupStatus = popup.getPopupStatus();
        this.companyName = popup.getHost().getCompanyName();
    }

    public static PopupPageResponseDto of(Popup popup) {
        return new PopupPageResponseDto(popup);
    }

}
