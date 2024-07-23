package com.sparta.ezpzuser.domain.cart.controller;


import static com.sparta.ezpzuser.common.util.ControllerUtil.getResponseEntity;

import com.sparta.ezpzuser.common.dto.CommonResponse;
import com.sparta.ezpzuser.common.security.UserDetailsImpl;
import com.sparta.ezpzuser.domain.cart.dto.CartCreateRequestDto;
import com.sparta.ezpzuser.domain.cart.dto.CartUpdateRequestDto;
import com.sparta.ezpzuser.domain.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/carts")
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CommonResponse<?>> createCart(
            @Valid @RequestBody CartCreateRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return getResponseEntity(cartService.createCart(requestDto, userDetails.getUser()),
                "장바구니 추가 성공");
    }

    @GetMapping
    public ResponseEntity<CommonResponse<?>> findCartsAll(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return getResponseEntity(cartService.findCartsAll(userDetails.getUser()), "장바구니 조회 성공");
    }

    @PatchMapping("/{cartId}")
    public ResponseEntity<CommonResponse<?>> updateCart(
            @PathVariable("cartId") Long cartId,
            @Valid @RequestBody CartUpdateRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return getResponseEntity(cartService.updateCart(cartId, requestDto, userDetails.getUser()),
                "장바구니 수량 변경 성공");
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<CommonResponse<?>> deleteCart(
            @PathVariable("cartId") Long cartId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        cartService.deleteCart(cartId, userDetails.getUser());
        return getResponseEntity("장바구니 삭제 성공");
    }
}
