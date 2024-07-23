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

    // Popup

    // Order

    // Item

    // Reservation
    RESERVATION_EXCEEDS_AVAILABLE_SLOTS(HttpStatus.BAD_REQUEST, "예약 가능 인원을 초과하였습니다."),
    RESERVATION_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 예약한 내역이 존재합니다."),
    
    // Slot
    SLOT_RESERVATION_FINISHED(HttpStatus.BAD_REQUEST, "예약이 마감된 슬롯입니다."),
    
    // Page
    INVALID_PAGE(HttpStatus.BAD_REQUEST, "페이지 번호가 올바르지 않습니다."),
    NOT_FOUND_PAGE(HttpStatus.NOT_FOUND, "페이지가 존재하지 않습니다.");
    
    //

    private final HttpStatus httpStatus;
    private final String message;

}