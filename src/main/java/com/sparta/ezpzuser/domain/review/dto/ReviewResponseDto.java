package com.sparta.ezpzuser.domain.review.dto;

import com.sparta.ezpzuser.domain.review.entity.Review;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewResponseDto {

    private final Long reviewId;
    private final int rating;
    private final String content;
    private final Long popupId;
    private final Long reservationId;
    private final LocalDateTime createdAt;

    private ReviewResponseDto(Review review) {
        this.reviewId = review.getId();
        this.rating = review.getRating();
        this.content = review.getContent();
        this.reservationId = review.getReservation().getId();
        this.popupId = review.getPopup().getId();
        this.createdAt = review.getCreatedAt();
    }

    public static ReviewResponseDto of(Review review) {
        return new ReviewResponseDto(review);
    }

}
