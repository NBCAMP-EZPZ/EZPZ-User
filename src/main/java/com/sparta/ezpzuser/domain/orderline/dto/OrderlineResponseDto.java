package com.sparta.ezpzuser.domain.orderline.dto;

import com.sparta.ezpzuser.domain.orderline.entity.Orderline;
import lombok.Getter;

@Getter
public class OrderlineResponseDto {

    private Long itemId;
    private int quantity;
    private int orderPrice;

    private OrderlineResponseDto(Long itemId, int quantity, int orderPrice) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.orderPrice = orderPrice;
    }

    public static OrderlineResponseDto of(Orderline orderline) {
        return new OrderlineResponseDto(
                orderline.getItem().getId(),
                orderline.getQuantity(),
                orderline.getOrderPrice()
        );
    }
}
