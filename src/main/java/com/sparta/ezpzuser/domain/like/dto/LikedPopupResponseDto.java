package com.sparta.ezpzuser.domain.like.dto;

import com.sparta.ezpzuser.domain.popup.entity.Popup;
import lombok.Getter;

@Getter
public class LikedPopupResponseDto {

    private final Long popupId;
    private final String name;
    private final int likeCount;
    private final String companyName;
    private final String thumbnail;

    private LikedPopupResponseDto(Popup popup) {
        this.popupId = popup.getId();
        this.name = popup.getName();
        this.likeCount = popup.getLikeCount();
        this.companyName = popup.getHost().getCompanyName();
        this.thumbnail = popup.getThumbnailUrl();
    }

    public static LikedPopupResponseDto of(Popup popup) {
        return new LikedPopupResponseDto(popup);
    }

}
