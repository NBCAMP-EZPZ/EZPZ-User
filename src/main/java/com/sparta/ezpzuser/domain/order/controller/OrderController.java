package com.sparta.ezpzuser.domain.order.controller;

import com.sparta.ezpzuser.common.dto.CommonResponse;
import com.sparta.ezpzuser.common.security.UserDetailsImpl;
import com.sparta.ezpzuser.domain.order.dto.OrderFindAllResponseDto;
import com.sparta.ezpzuser.domain.order.dto.OrderRequestDto;
import com.sparta.ezpzuser.domain.order.dto.OrderResponseDto;
import com.sparta.ezpzuser.domain.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.sparta.ezpzuser.common.util.ControllerUtil.getResponseEntity;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<CommonResponse<?>> createOrder(
            @Valid @RequestBody OrderRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        OrderResponseDto response = orderService.createOrder(requestDto, userDetails.getUser());
        return getResponseEntity(response, "주문 완료");
    }

    @GetMapping
    public ResponseEntity<CommonResponse<?>> findOrdersAll(
            Pageable pageable,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Page<OrderFindAllResponseDto> response = orderService.findOrdersAll(pageable, userDetails.getUser());
        return getResponseEntity(response, "주문 목록 조회 성공");
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<CommonResponse<?>> findOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        OrderResponseDto response = orderService.findOrder(orderId, userDetails.getUser());
        return getResponseEntity(response, "주문 상세 조회 성공");
    }

    @PatchMapping("/{orderId}")
    public ResponseEntity<CommonResponse<?>> deleteOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        orderService.deleteOrder(orderId, userDetails.getUser());
        return getResponseEntity("주문 취소 성공");
    }

}
