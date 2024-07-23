package com.sparta.ezpzuser.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorType {

    // JWT
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다. 다시 로그인 해주세요."),
    CARD_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, "카드 작성자 및 매니저만 접근할 수 있습니다."),

    // User
    DUPLICATED_USERNAME(HttpStatus.BAD_REQUEST, "이미 존재하는 사용자 아이디입니다."),

    // Popup

    // Order

    // Item

    // Reservation

    // Cart
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "재고량이 부족합니다."),
    STOCK_UNAVAILABLE(HttpStatus.BAD_REQUEST, "장바구니에 담긴 상품의 재고가 현재 없거나 부족합니다."),
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 장바구니 내역입니다."),
    UNAUTHORIZED_CART_ACCESS(HttpStatus.FORBIDDEN, "본인의 장바구니만 수정할 수 있습니다.");

    ;
    private final HttpStatus httpStatus;
    private final String message;

}