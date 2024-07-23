package com.sparta.ezpzuser.domain.cart.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CartCreateRequestDto {

    @NotBlank(message = "장바구니에 담을 상품의 ID를 입력해주세요.")
    Long itemId;

    @NotBlank(message = "장바구니에 담을 상품의 수량을 입력해주세요.")
    Long quantity;
}
