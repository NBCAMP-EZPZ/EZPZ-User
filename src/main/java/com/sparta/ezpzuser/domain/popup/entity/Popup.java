package com.sparta.ezpzuser.domain.popup.entity;

import com.sparta.ezpzuser.common.entity.Timestamped;
import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.domain.host.entity.Host;
import com.sparta.ezpzuser.domain.popup.enums.ApprovalStatus;
import com.sparta.ezpzuser.domain.popup.enums.PopupStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.sparta.ezpzuser.common.exception.ErrorType.NOT_LIKED_POPUP;
import static com.sparta.ezpzuser.common.exception.ErrorType.POPUP_ACCESS_FORBIDDEN;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Popup extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "popup_id")
    private Long id;

    private String name;

    private String description;

    private String thumbnailUrl;

    private String thumbnailName;

    private String address;

    private String managerName;

    private String phoneNumber;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private int likeCount;

    private int reviewCount;

    private double ratingAvg;

    @Transient
    private int ratingSum = 0;

    @Enumerated(EnumType.STRING)
    private PopupStatus popupStatus;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private Host host;

    private Popup(Host host, int likeCount) {
        this.host = host;
        this.likeCount = likeCount;
        this.reviewCount = 0;
        this.ratingAvg = 0;
        this.popupStatus = PopupStatus.IN_PROGRESS;
        this.approvalStatus = ApprovalStatus.APPROVED;
        this.startDate = LocalDateTime.now();
        this.endDate = LocalDateTime.now().plusDays(1);
    }

    public static Popup createMockPopup() {
        return new Popup(null, 0);
    }

    public static Popup createMockLikedPopup(int likeCount) {
        return new Popup(null, likeCount);
    }

    /**
     * 상태 확인
     */
    public void verifyStatus() {
        if (!this.approvalStatus.equals(ApprovalStatus.APPROVED)
                || this.popupStatus.equals(PopupStatus.CANCELED)) {
            throw new CustomException(POPUP_ACCESS_FORBIDDEN);
        }
    }

    /**
     * 리뷰 추가
     *
     * @param rating 리뷰 별점
     */
    public void addReview(int rating) {
        this.reviewCount++;
        this.ratingSum += rating;
        this.ratingAvg = (double) ratingSum / this.reviewCount;
    }

    /**
     * 좋아요 개수 증감
     *
     * @param isLike 좋아요 여부 (true: 좋아요, false: 좋아요 취소)
     */
    public void updateLikeCount(boolean isLike) {
        if (isLike) {
            this.likeCount++;
        } else {
            if (this.likeCount == 0) {
                throw new CustomException(NOT_LIKED_POPUP);
            }
            this.likeCount--;
        }
    }

}
