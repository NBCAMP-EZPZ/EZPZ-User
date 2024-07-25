package com.sparta.ezpzuser.domain.popup.dto;

import lombok.Getter;

@Getter
public class PopupCondition {

    private final String popupStatus;

    private PopupCondition(String popupStatus) {
        this.popupStatus = popupStatus;
    }

    public static PopupCondition of(String popupStatus) {
        return new PopupCondition(popupStatus);
    }
}
