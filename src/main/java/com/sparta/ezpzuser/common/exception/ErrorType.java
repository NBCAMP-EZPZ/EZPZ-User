package com.sparta.ezpzuser.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static com.sparta.ezpzuser.common.resolver.CustomPageableHandlerMethodArgumentResolver.MAX_PAGE_SIZE;
import static org.springframework.http.HttpStatus.*;

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
    PAGE_NOT_FOUND(NOT_FOUND, "해당 페이지를 찾을 수 없습니다."),

    // JWT
    INVALID_REFRESH_TOKEN(UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다. 다시 로그인 해주세요."),
    CARD_ACCESS_FORBIDDEN(FORBIDDEN, "카드 작성자 및 매니저만 접근할 수 있습니다."),

    // User
    DUPLICATED_USERNAME(BAD_REQUEST, "이미 존재하는 사용자 아이디입니다."),

    // Popup

    // Order

    // Item

    // Reservation
    RESERVATION_EXCEEDS_AVAILABLE_SLOTS(BAD_REQUEST, "예약 가능 인원을 초과하였습니다."),
    RESERVATION_ALREADY_EXISTS(BAD_REQUEST, "이미 예약한 내역이 존재합니다."),

    // Review
    RESERVATION_NOT_FOUND(NOT_FOUND, "해당 예약을 찾을 수 없습니다."),
    DIFFERENT_RESERVATION_USER(UNAUTHORIZED, "해당 예약의 예약자가 아닙니다."),
    UNVISITED_USER(BAD_REQUEST, "예약한 팝업에 방문 완료한 예약자가 아닙니다."),

    // Slot
    SLOT_RESERVATION_FINISHED(BAD_REQUEST, "예약이 마감된 슬롯입니다."),
    SLOT_NOT_FOUND(NOT_FOUND, "해당 슬롯을 찾을 수 없습니다."),

    // Coupon
    COUPON_NOT_FOUND(NOT_FOUND, "해당 쿠폰을 찾을 수 없습니다."),
    ALREADY_DOWNLOADED_COUPON(BAD_REQUEST, "이미 다운로드 받은 쿠폰입니다."),
    SOLD_OUT_COUPON(BAD_REQUEST, "이미 매진된 쿠폰입니다."),

    ;
    private final HttpStatus httpStatus;
    private final String message;

}