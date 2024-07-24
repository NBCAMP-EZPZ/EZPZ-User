package com.sparta.ezpzuser.domain.order.dto;

import com.sparta.ezpzuser.domain.order.entity.Order;
import com.sparta.ezpzuser.domain.order.enums.OrderStatus;
import com.sparta.ezpzuser.domain.orderline.dto.OrderlineResponseDto;
import java.util.List;
import lombok.Getter;

@Getter
public class OrderResponseDto {

    private Long orderId;
    private int totalPrice;
    private OrderStatus orderStatus;
    private List<OrderlineResponseDto> responseDtos;

    private OrderResponseDto(Long orderId, int totalPrice, OrderStatus orderStatus,
            List<OrderlineResponseDto> responseDtos) {
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
        this.responseDtos = responseDtos;
    }

    public static OrderResponseDto of(Order order, List<OrderlineResponseDto> responseDtos) {
        return new OrderResponseDto(
                order.getId(),
                order.getTotalPrice(),
                order.getOrderStatus(),
                responseDtos
        );
    }
}
