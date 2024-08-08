package com.sparta.ezpzuser.domain.cart.dto;

import com.sparta.ezpzuser.domain.cart.entity.Cart;
import lombok.Getter;

@Getter
public class CartResponseDto {

    private final Long cartId;
    private final Long itemId;
    private final int quantity;

    private CartResponseDto(Cart cart) {
        this.cartId = cart.getId();
        this.itemId = cart.getItem().getId();
        this.quantity = cart.getQuantity();
    }

    public static CartResponseDto of(Cart cart) {
        return new CartResponseDto(cart);
    }

}
