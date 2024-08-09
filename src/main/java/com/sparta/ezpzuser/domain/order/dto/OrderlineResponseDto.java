package com.sparta.ezpzuser.domain.order.dto;

import com.sparta.ezpzuser.domain.order.entity.Orderline;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderlineResponseDto {

    private Long itemId;
    private int quantity;
    private int orderPrice;

    private OrderlineResponseDto(Orderline orderline) {
        this.itemId = orderline.getItem().getId();
        this.quantity = orderline.getQuantity();
        this.orderPrice = orderline.getOrderPrice();
    }

    public static OrderlineResponseDto of(Orderline orderline) {
        return new OrderlineResponseDto(orderline);
    }

}
