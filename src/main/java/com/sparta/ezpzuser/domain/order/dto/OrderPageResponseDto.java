package com.sparta.ezpzuser.domain.order.dto;

import com.sparta.ezpzuser.domain.order.entity.Order;
import com.sparta.ezpzuser.domain.order.enums.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderPageResponseDto {

    private final Long orderId;
    private final OrderStatus orderStatus;
    private final int totalPrice;
    private final LocalDateTime orderedAt;

    private OrderPageResponseDto(Order order) {
        this.orderId = order.getId();
        this.totalPrice = order.getTotalPrice();
        this.orderStatus = order.getOrderStatus();
        this.orderedAt = order.getCreatedAt();
    }

    public static OrderPageResponseDto of(Order order) {
        return new OrderPageResponseDto(order);
    }

}
