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
    POPUP_NOT_FOUNT(HttpStatus.NOT_FOUND, "존재하지 않는 팝업입니다."),
    POPUP_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 팝업이 존재하지 않거나, 팝업에 대한 권한이 없습니다."),

    // Order

    // Item

    // Reservation

    //

    ;
    private final HttpStatus httpStatus;
    private final String message;

}