package com.sparta.ezpzuser.domain.item.dto;

import lombok.Getter;

@Getter
public class ItemCondition {
    
    private final String popupId;
    private final String itemStatus;

    private ItemCondition(String popupId, String itemStatus) {
        this.popupId = popupId;
        this.itemStatus = itemStatus;
    }

    public static ItemCondition of(String popupId, String itemStatus) {
        return new ItemCondition(popupId, itemStatus);
    }
}
