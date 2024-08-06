package com.sparta.ezpzuser.domain.cart.dto;

import com.sparta.ezpzuser.domain.cart.entity.Cart;
import lombok.Getter;

@Getter
public class CartResponseDto {

    private final Long cartId;
    private final Long itemId;
    private final String itemName;
    private final int itemPrice;
    private final int quantity;
    private final String imageUrl;

    private CartResponseDto(Cart cart) {
        this.cartId = cart.getId();
        this.itemId = cart.getItem().getId();
        this.itemName = cart.getItem().getName();
        this.itemPrice = cart.getItem().getPrice();
        this.quantity = cart.getQuantity();
        this.imageUrl = cart.getItem().getImageUrl();
    }

    public static CartResponseDto of(Cart cart) {
        return new CartResponseDto(cart);
    }

}
