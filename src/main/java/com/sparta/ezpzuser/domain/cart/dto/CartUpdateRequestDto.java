package com.sparta.ezpzuser.domain.cart.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartUpdateRequestDto {
    
    @NotBlank(message = "장바구니에 담을 상품의 수량을 입력해주세요.")
    Long quantity;
}
