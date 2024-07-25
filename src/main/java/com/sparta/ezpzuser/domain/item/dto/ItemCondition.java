package com.sparta.ezpzuser.domain.item.dto;

import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.common.exception.ErrorType;
import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public class ItemCondition {

    private final String hostId;
    private final String popupId;
    private final String itemStatus;

    private ItemCondition(String hostId, String popupId, String itemStatus) {
        this.hostId = hostId;
        this.popupId = popupId;
        this.itemStatus = itemStatus;
    }

    public static ItemCondition of(String hostId, String popupId, String itemStatus) {
        if (!StringUtils.hasText(itemStatus) || !itemStatus.equals("before_sale") &&
                !itemStatus.equals("sale") && !itemStatus.equals("sold_out")) {
            throw new CustomException(ErrorType.ITEM_ACCESS_FORBIDDEN);
        }
        return new ItemCondition(hostId, popupId, itemStatus);
    }
}
