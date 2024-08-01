package com.sparta.ezpzuser.domain.cart.dto;

import com.sparta.ezpzuser.domain.cart.entity.Cart;
import lombok.Getter;

@Getter
public class CartResponseDto {

    private Long cartId;
    private Long itemId;
    private int quantity;
    
    //필요에 의해 추가
    private String itemName;
    private int itemPrice;
    private String imageUrl;
    private Long cartId;

    /**
     * CartResponseDto 생성자
     *
     * @param cart
     */
    private CartResponseDto(Cart cart) {
        this.cartId = cart.getId();
        this.itemId = cart.getItem().getId();
        this.quantity = cart.getQuantity();
        this.itemName = cart.getItem().getName();
        this.itemPrice = cart.getItem().getPrice();
        this.imageUrl = cart.getItem().getImageUrl();
        this.cartId = cart.getId();
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
