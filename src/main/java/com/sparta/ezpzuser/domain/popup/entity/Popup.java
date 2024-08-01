package com.sparta.ezpzuser.domain.popup.entity;

import com.sparta.ezpzuser.common.entity.Timestamped;
import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.common.exception.ErrorType;
import com.sparta.ezpzuser.domain.host.entity.Host;
import com.sparta.ezpzuser.domain.popup.enums.ApprovalStatus;
import com.sparta.ezpzuser.domain.popup.enums.PopupStatus;
import com.sparta.ezpzuser.domain.review.entity.Review;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Popup extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "popup_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private Host host;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "thumbnail_url", nullable = false)
    private String thumbnailUrl;

    @Column(name = "thumbnail_name", nullable = false)
    private String thumbnailName;

    @Column(nullable = false)
    private String address;

    @Column(name = "manager_name", nullable = false)
    private String managerName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "popup_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PopupStatus popupStatus;

    @Column(name = "approval_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    @Column(name = "like_count", nullable = false)
    private int likeCount;

    @Column(name = "review_count", nullable = false)
    private int reviewCount;

    @Column(name = "rating_avg", nullable = false)
    private float ratingAvg;

    @Transient
    private int ratingSum;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    /**
     * 상태 확인
     */
    public void verifyStatus() {
        if (this.approvalStatus == ApprovalStatus.REVIEWING ||
                this.popupStatus == PopupStatus.CANCELED) {
            throw new CustomException(ErrorType.POPUP_ACCESS_FORBIDDEN);
        }
    }

    /**
     * 리뷰 추가
     * @param review 추가할 
     */
    public void addReview(Review review) {
        this.reviewCount++;
        this.ratingSum += review.getRating();
        this.ratingAvg = (float) ratingSum / this.reviewCount;
    }


    /**
     * 좋아요 개수 (true: 증가 / false: 감소)
     * @param b boolean
     */
    public void updateLikeCount(boolean b) {
        if (b) {
            this.likeCount++;
        }else {
            if (this.likeCount > 0) {
                this.likeCount--;
            }
        }
    }

}
