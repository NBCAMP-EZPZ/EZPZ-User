package com.sparta.ezpzuser.domain.item.dto;

import com.sparta.ezpzuser.domain.item.enums.ItemStatus;
import lombok.Getter;

@Getter
public class ItemCondition {

    private final Long popupId;
    private final ItemStatus itemStatus;

    private ItemCondition(Long popupId, String itemStatus) {
        this.popupId = popupId;
        this.itemStatus = ItemStatus.valueOf(itemStatus.toUpperCase());
    }

    public static ItemCondition of(Long popupId, String itemStatus) {
        return new ItemCondition(popupId, itemStatus);
    }

}
