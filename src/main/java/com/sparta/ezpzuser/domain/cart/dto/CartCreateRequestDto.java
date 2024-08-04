package com.sparta.ezpzuser.domain.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CartCreateRequestDto {

    @NotNull(message = "장바구니에 담을 상품의 ID를 입력해주세요.")
    private Long itemId;

    @NotNull(message = "장바구니에 담을 상품의 수량을 입력해주세요.")
    @Min(value = 0, message = "장바구니에 담을 상품의 수량은 0일 수 없습니다.")
    private int quantity;

}
