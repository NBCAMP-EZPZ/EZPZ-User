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

    // Coupon
    COUPON_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 쿠폰입니다."),
    ALREADY_DOWNLOADED_COUPON(HttpStatus.BAD_REQUEST, "이미 다운로드 받은 쿠폰입니다."),
    SOLD_OUT_COUPON(HttpStatus.BAD_REQUEST, "이미 매진된 쿠폰입니다.")

    ;
    private final HttpStatus httpStatus;
    private final String message;

}