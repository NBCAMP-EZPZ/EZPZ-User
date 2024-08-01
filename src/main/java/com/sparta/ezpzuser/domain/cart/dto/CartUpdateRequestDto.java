package com.sparta.ezpzuser.domain.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartUpdateRequestDto {

    @NotNull(message = "장바구니에 담을 상품의 수량을 입력해주세요.")
    @Min(value = 0, message = "0 이상의 숫자를 입력해주세요.")
    int quantity;
}
