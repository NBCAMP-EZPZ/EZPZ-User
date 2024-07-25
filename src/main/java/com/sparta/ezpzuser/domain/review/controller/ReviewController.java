package com.sparta.ezpzuser.domain.review.controller;

import com.sparta.ezpzuser.common.dto.CommonResponse;
import com.sparta.ezpzuser.common.security.UserDetailsImpl;
import com.sparta.ezpzuser.domain.review.dto.ReviewRequestDto;
import com.sparta.ezpzuser.domain.review.dto.ReviewResponseDto;
import com.sparta.ezpzuser.domain.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.sparta.ezpzuser.common.util.ControllerUtil.getResponseEntity;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 등록
     *
     * @param dto         리뷰 생성에 필요한 dto
     * @param userDetails 리뷰 남길 이용자 정보
     * @return 생성된 리뷰 정보
     */
    @PostMapping("/reviews")
    public ResponseEntity<CommonResponse<?>> createReview(
            @Valid @RequestBody ReviewRequestDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        ReviewResponseDto response = reviewService.createReview(dto, userDetails.getUser());
        return getResponseEntity(response, "리뷰 등록 성공");
    }

    /**
     * 해당 팝업의 전체 리뷰 목록 조회
     *
     * @param popupId  팝업 id
     * @param pageable Pageable 객체
     * @return 리뷰 정보 목록
     */
    @GetMapping("/popups/{popupId}/reviews")
    public ResponseEntity<CommonResponse<?>> createReview(
            @PathVariable Long popupId,
            @PageableDefault(
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {

        Page<ReviewResponseDto> response = reviewService.findAllPopupReviews(popupId, pageable);
        return getResponseEntity(response, "해당 팝업의 전체 리뷰 목록 조회 성공");
    }

}
