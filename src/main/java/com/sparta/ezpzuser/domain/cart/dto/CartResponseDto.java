package com.sparta.ezpzuser.domain.cart.dto;

import com.sparta.ezpzuser.domain.cart.entity.Cart;
import lombok.Getter;

@Getter
public class CartResponseDto {

    private Long itemId;
    private int quantity;

    /**
     * CartResponseDto 생성자
     *
     * @param cart
     */
    private CartResponseDto(Cart cart) {
        this.itemId = cart.getItem().getId();
        this.quantity = cart.getQuantity();
    }

    /**
     * CartResponseDto 정적 팩토리 메서드
     *
     * @param cart 장바구니
     * @return CartResponseDto 생성자
     */
    public static CartResponseDto of(Cart cart) {
        return new CartResponseDto(cart);
    }
}
