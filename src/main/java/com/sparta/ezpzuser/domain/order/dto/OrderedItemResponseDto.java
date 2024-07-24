package com.sparta.ezpzuser.domain.order.dto;

import lombok.Getter;

@Getter
public class OrderedItemResponseDto {

    private Long itemId;
    private int quantity;

    private OrderedItemResponseDto(Long itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public static OrderedItemResponseDto of(Long itemId, int quantity) {
        return new OrderedItemResponseDto(itemId, quantity);
    }
}
