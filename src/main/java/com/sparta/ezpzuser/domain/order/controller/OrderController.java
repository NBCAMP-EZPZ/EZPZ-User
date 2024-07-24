package com.sparta.ezpzuser.domain.order.controller;

import static com.sparta.ezpzuser.common.util.ControllerUtil.getResponseEntity;

import com.sparta.ezpzuser.common.dto.CommonResponse;
import com.sparta.ezpzuser.common.security.UserDetailsImpl;
import com.sparta.ezpzuser.domain.order.dto.OrderRequestDto;
import com.sparta.ezpzuser.domain.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<CommonResponse<?>> createOrder(
            @Valid @RequestBody OrderRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return getResponseEntity(orderService.createOrder(requestDto, userDetails.getUser()),
                "주문 완료");
    }


}
