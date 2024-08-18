package com.sparta.ezpzuser.domain.coupon.controller;

import com.sparta.ezpzuser.common.dto.CommonResponse;
import com.sparta.ezpzuser.common.security.UserDetailsImpl;
import com.sparta.ezpzuser.domain.coupon.dto.CouponResponseDto;
import com.sparta.ezpzuser.domain.coupon.dto.UserCouponResponseDto;
import com.sparta.ezpzuser.domain.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.sparta.ezpzuser.common.util.ControllerUtil.getResponseEntity;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CouponController {

    private final CouponService couponService;

    /**
     * 쿠폰 다운로드
     *
     * @param couponId    다운로드할 쿠폰 id
     * @param userDetails 다운로드 요청한 이용자 정보
     * @return 다운로드 후 생성된 UserCoupon 정보와 응답 메시지를 포함한 ResponseEntity
     */
    @PostMapping("/coupons/{couponId}")
    public ResponseEntity<CommonResponse<?>> downloadCoupon(
            @PathVariable Long couponId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        UserCouponResponseDto response = couponService.downloadCoupon(couponId, userDetails.getUser());
        return getResponseEntity(response, "쿠폰 다운로드 성공");
    }

    /**
     * 다운로드 가능한 쿠폰 목록 조회
     *
     * @param pageable Pageable 객체
     * @return 다운로드 가능한 쿠폰 목록과 응답 메시지를 포함한 ResponseEntity
     */
    @GetMapping("/coupons")
    public ResponseEntity<CommonResponse<?>> findAllDownloadableCoupons(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Pageable pageable) {

        Page<CouponResponseDto> response = couponService.findAllDownloadableCoupons(userDetails.getUser(), pageable);
        return getResponseEntity(response, "다운로드 가능한 쿠폰 목록 조회 성공");
    }

    /**
     * 마이 쿠폰 목록 조회
     *
     * @param pageable    Pageable 객체
     * @param userDetails 다운로드 요청한 이용자 정보
     * @return 마이 쿠폰 목록과 응답 메시지를 포함한 ResponseEntity
     */
    @GetMapping("/users/coupons")
    public ResponseEntity<CommonResponse<?>> findAllMyCoupons(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Pageable pageable) {

        Page<CouponResponseDto> response = couponService.findAllMyCoupons(userDetails.getUser(), pageable);
        return getResponseEntity(response, "마이 쿠폰 목록 조회 성공");
    }

}
