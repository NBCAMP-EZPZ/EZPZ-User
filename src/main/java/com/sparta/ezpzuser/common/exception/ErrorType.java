package com.sparta.ezpzuser.common.exception;

import static com.sparta.ezpzuser.common.resolver.CustomPageableHandlerMethodArgumentResolver.MAX_PAGE_SIZE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorType {

    // Page
    INVALID_PAGE_NUMBER_FORMAT(BAD_REQUEST, "숫자 형식이 아닌 페이지 number입니다."),
    INVALID_PAGE_SIZE_FORMAT(BAD_REQUEST, "숫자 형식이 아닌 페이지 size입니다."),
    INVALID_PAGE_NUMBER(BAD_REQUEST, "페이지 number는 음수일 수 없습니다."),
    INVALID_PAGE_SIZE(BAD_REQUEST, "페이지 size는 0 이하일 수 없습니다."),
    EXCEED_MAX_PAGE_SIZE(BAD_REQUEST, "페이지 size는 " + MAX_PAGE_SIZE + "을 초과할 수 없습니다."),
    EMPTY_PAGE_ELEMENTS(BAD_REQUEST, "페이지의 요소가 존재하지 않습니다."),
    PAGE_NOT_FOUND(BAD_REQUEST, "존재하지 않는 페이지입니다."),

    // JWT
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다. 다시 로그인 해주세요."),
    CARD_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, "카드 작성자 및 매니저만 접근할 수 있습니다."),

    // User
    DUPLICATED_USERNAME(HttpStatus.BAD_REQUEST, "이미 존재하는 사용자 아이디입니다."),

    // Popup

    // Order
    ORDER_NOT_FOUND(BAD_REQUEST, "존재하지 않는 주문 내역입니다."),

    // Item

    // Reservation

    // Coupon
    COUPON_NOT_FOUND(BAD_REQUEST, "존재하지 않는 쿠폰입니다."),
    ALREADY_DOWNLOADED_COUPON(BAD_REQUEST, "이미 다운로드 받은 쿠폰입니다."),
    SOLD_OUT_COUPON(BAD_REQUEST, "이미 매진된 쿠폰입니다."),

    // Cart
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "재고량이 부족합니다."),
    STOCK_UNAVAILABLE(HttpStatus.BAD_REQUEST, "장바구니에 담긴 상품의 재고가 현재 없거나 부족합니다."),
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 장바구니 내역입니다."),
    UNAUTHORIZED_CART_ACCESS(HttpStatus.FORBIDDEN, "본인의 장바구니만 수정할 수 있습니다.");;
    private final HttpStatus httpStatus;
    private final String message;

}