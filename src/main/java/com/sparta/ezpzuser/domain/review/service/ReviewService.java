package com.sparta.ezpzuser.domain.review.service;

import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.common.lock.DistributedLock;
import com.sparta.ezpzuser.common.util.PageUtil;
import com.sparta.ezpzuser.domain.reservation.entity.Reservation;
import com.sparta.ezpzuser.domain.reservation.repository.ReservationRepository;
import com.sparta.ezpzuser.domain.review.dto.ReviewRequestDto;
import com.sparta.ezpzuser.domain.review.dto.ReviewResponseDto;
import com.sparta.ezpzuser.domain.review.entity.Review;
import com.sparta.ezpzuser.domain.review.repository.ReviewRepository;
import com.sparta.ezpzuser.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sparta.ezpzuser.common.exception.ErrorType.ALREADY_REVIEWED_POPUP;
import static com.sparta.ezpzuser.common.exception.ErrorType.RESERVATION_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;

    /**
     * 리뷰 등록
     *
     * @param dto  리뷰 생성에 필요한 dto
     * @param user 리뷰 남길 이용자
     * @return 생성된 리뷰 정보
     */
    @DistributedLock(key = "'createReview'")
    public ReviewResponseDto createReview(ReviewRequestDto dto, User user) {
        Reservation reservation = reservationRepository.findByIdWithSlotAndPopup(dto.getReservationId())
                .orElseThrow(() -> new CustomException(RESERVATION_NOT_FOUND));
        reservation.verifyReviewAuthority(user);
        // 이미 해당 팝업에 리뷰를 작성한 경우
        if (reviewRepository.existsByReservation(reservation)) {
            throw new CustomException(ALREADY_REVIEWED_POPUP);
        }
        Review review = Review.of(dto, reservation);
        reviewRepository.save(review);
        return ReviewResponseDto.of(review);
    }

    /**
     * 해당 팝업의 전체 리뷰 목록 조회
     *
     * @param popupId  팝업 id
     * @param pageable Pageable 객체
     * @return 리뷰 정보 목록
     */
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> findAllPopupReviews(Long popupId, Pageable pageable) {
        Page<Review> page = reviewRepository.findByPopupIdOrderByCreatedAtDesc(popupId, pageable);
        PageUtil.validatePageableWithPage(pageable, page);
        return page.map(ReviewResponseDto::of);
    }

}
