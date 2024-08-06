package com.sparta.ezpzuser.domain.order.dto;

import com.sparta.ezpzuser.domain.order.entity.Order;
import com.sparta.ezpzuser.domain.order.entity.Orderline;
import com.sparta.ezpzuser.domain.order.enums.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderResponseDto {

    private final Long orderId;
    private final int totalPrice;
    private final OrderStatus orderStatus;
    private final LocalDateTime orderedAt;
    private final List<OrderlineResponseDto> orderedItems;

    private OrderResponseDto(Order order, List<Orderline> orderlineList) {
        this.orderId = order.getId();
        this.totalPrice = order.getTotalPrice();
        this.orderStatus = order.getOrderStatus();
        this.orderedAt = order.getCreatedAt();
        this.orderedItems = orderlineList.stream()
                .map(OrderlineResponseDto::of)
                .toList();
    }

    public static OrderResponseDto of(Order order, List<Orderline> orderlineList) {
        return new OrderResponseDto(order, orderlineList);
    }

}