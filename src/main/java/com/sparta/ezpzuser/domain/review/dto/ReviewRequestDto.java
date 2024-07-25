package com.sparta.ezpzuser.domain.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReviewRequestDto {

    @NotNull(message = "reservationId는 필수 값입니다.")
    private Long reservationId;

    @NotNull(message = "평점은 필수 값입니다.")
    @Min(value = 1, message = "평점은 최소 1점이어야 합니다.")
    @Max(value = 5, message = "평점은 최대 5점까지 가능합니다.")
    private int rating;

    @NotBlank(message = "리뷰 내용은 공백일 수 없습니다.")
    private String content;

}
