package com.sparta.ezpzuser.domain.order.dto;

import com.sparta.ezpzuser.domain.order.entity.Order;
import com.sparta.ezpzuser.domain.order.entity.Orderline;
import com.sparta.ezpzuser.domain.order.enums.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrderResponseDto {

    private Long orderId;
    private int totalPrice;
    private OrderStatus orderStatus;
    private String orderedAt;
    private List<OrderlineResponseDto> orderedItems;

    private OrderResponseDto(Order order, List<Orderline> orderlineList) {
        this.orderId = order.getId();
        this.totalPrice = order.getTotalPrice();
        this.orderStatus = order.getOrderStatus();
        this.orderedAt = order.getCreatedAt().toString();
        this.orderedItems = orderlineList.stream()
                .map(OrderlineResponseDto::of)
                .toList();
    }

    public static OrderResponseDto of(Order order, List<Orderline> orderlineList) {
        return new OrderResponseDto(order, orderlineList);
    }

}