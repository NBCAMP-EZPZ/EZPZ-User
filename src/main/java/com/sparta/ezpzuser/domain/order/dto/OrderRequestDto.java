package com.sparta.ezpzuser.domain.order.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Getter;

@Getter
public class OrderRequestDto {

    @Getter
    public static class CartIdRequest {

        private Long cartId;
    }

    @NotEmpty(message = "최소 1개 이상의 장바구니를 선택해주세요.")
    List<CartIdRequest> cartIdRequestList;
}
