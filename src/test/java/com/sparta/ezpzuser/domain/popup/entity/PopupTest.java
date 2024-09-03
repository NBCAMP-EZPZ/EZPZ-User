package com.sparta.ezpzuser.domain.popup.entity;

import com.sparta.ezpzuser.common.exception.CustomException;
import org.junit.jupiter.api.Test;

import static com.sparta.ezpzuser.common.exception.ErrorType.NOT_LIKED_POPUP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PopupTest {

    @Test
    void 리뷰_추가() {
        // given
        Popup popup = Popup.createMockPopup();

        // when
        int rating1 = 5;
        int rating2 = 4;
        popup.addReview(rating1);
        popup.addReview(rating2);

        // then
        double expectedRatingAvg = (double) (rating1 + rating2) / 2;
        assertThat(popup.getRatingAvg()).isEqualTo(expectedRatingAvg);
        assertThat(popup.getReviewCount()).isEqualTo(2);
    }

    @Test
    void 좋아요_증가() {
        // given
        int likeCount = 10;
        Popup popup = Popup.createMockLikedPopup(likeCount);

        // when
        popup.updateLikeCount(true);

        // then
        assertThat(popup.getLikeCount()).isEqualTo(likeCount + 1);
    }

    @Test
    void 좋아요_감소_성공() {
        // given
        int likeCount = 10;
        Popup popup = Popup.createMockLikedPopup(likeCount);

        // when
        popup.updateLikeCount(false);

        // then
        assertThat(popup.getLikeCount()).isEqualTo(likeCount - 1);
    }

    @Test
    void 좋아요_감소_실패() {
        // given
        Popup popup = Popup.createMockLikedPopup(0);

        // when
        CustomException exception = assertThrows(CustomException.class, () ->
                popup.updateLikeCount(false)
        );

        // then
        assertThat(exception.getErrorType()).isEqualTo(NOT_LIKED_POPUP);
    }

}