package com.sparta.ezpzuser.domain.order.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrderRequestDto {

    @NotEmpty(message = "최소 1개 이상의 장바구니를 선택해주세요.")
    List<Long> cartIdList;

}
