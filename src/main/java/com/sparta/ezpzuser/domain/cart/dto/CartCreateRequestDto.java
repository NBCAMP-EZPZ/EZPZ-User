package com.sparta.ezpzuser.domain.cart.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CartCreateRequestDto {

    @NotNull(message = "장바구니에 담을 상품의 ID를 입력해주세요.")
    Long itemId;

    @NotNull(message = "장바구니에 담을 상품의 수량을 입력해주세요.")
    int quantity;
}
