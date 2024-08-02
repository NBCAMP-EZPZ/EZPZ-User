package com.sparta.ezpzuser.domain.cart.controller;


import com.sparta.ezpzuser.common.dto.CommonResponse;
import com.sparta.ezpzuser.common.security.UserDetailsImpl;
import com.sparta.ezpzuser.domain.cart.dto.CartCreateRequestDto;
import com.sparta.ezpzuser.domain.cart.dto.CartResponseDto;
import com.sparta.ezpzuser.domain.cart.dto.CartUpdateRequestDto;
import com.sparta.ezpzuser.domain.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.sparta.ezpzuser.common.util.ControllerUtil.getResponseEntity;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/carts")
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CommonResponse<?>> createCart(
            @Valid @RequestBody CartCreateRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        CartResponseDto response = cartService.createCart(requestDto, userDetails.getUser());
        return getResponseEntity(response, "장바구니 추가 성공");
    }

    @GetMapping
    public ResponseEntity<CommonResponse<?>> findCartsAll(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        List<CartResponseDto> response = cartService.findCartsAll(userDetails.getUser());
        return getResponseEntity(response, "장바구니 조회 성공");
    }

    @PatchMapping("/{cartId}")
    public ResponseEntity<CommonResponse<?>> updateCart(
            @PathVariable("cartId") Long cartId,
            @Valid @RequestBody CartUpdateRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        CartResponseDto response = cartService.updateCart(cartId, requestDto, userDetails.getUser());
        return getResponseEntity(response, "장바구니 수량 변경 성공");
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<CommonResponse<?>> deleteCart(
            @PathVariable("cartId") Long cartId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        cartService.deleteCart(cartId, userDetails.getUser());
        return getResponseEntity("장바구니 삭제 성공");
    }

}
