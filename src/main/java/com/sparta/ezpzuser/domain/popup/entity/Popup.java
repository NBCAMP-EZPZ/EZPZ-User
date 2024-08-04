package com.sparta.ezpzuser.domain.popup.entity;

import com.sparta.ezpzuser.common.entity.Timestamped;
import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.domain.host.entity.Host;
import com.sparta.ezpzuser.domain.popup.enums.ApprovalStatus;
import com.sparta.ezpzuser.domain.popup.enums.PopupStatus;
import com.sparta.ezpzuser.domain.review.entity.Review;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    private Popup(Host host, String name, String description, String thumbnailUrl, String thumbnailName, String address, String managerName, String phoneNumber, PopupStatus popupStatus, ApprovalStatus approvalStatus, LocalDateTime startDate, LocalDateTime endDate) {
        this.host = host;
        this.name = name;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.thumbnailName = thumbnailName;
        this.address = address;
        this.managerName = managerName;
        this.phoneNumber = phoneNumber;
        this.popupStatus = popupStatus;
        this.approvalStatus = approvalStatus;
        this.likeCount = 0;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static Popup of(Host host, String name, String description, String thumbnailUrl, String thumbnailName, String address, String managerName, String phoneNumber, PopupStatus popupStatus, ApprovalStatus approvalStatus, LocalDateTime startDate, LocalDateTime endDate) {
        return new Popup(host, name, description, thumbnailUrl, thumbnailName, address, managerName, phoneNumber, popupStatus, approvalStatus, startDate, endDate);
    }

    // 테스트용 생성자
    private Popup(Host host) {
        this.host = host;
        this.approvalStatus = ApprovalStatus.APPROVED;
        this.popupStatus = PopupStatus.IN_PROGRESS;
        this.startDate = LocalDateTime.now();
        this.endDate = LocalDateTime.now().plusDays(1);
    }

    public static Popup createMockPopup() {
        return new Popup(null);
    }

    public static Popup createMockPopup(Host host) {
        return new Popup(host);
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
     * @param review 추가할 리뷰
     */
    public void addReview(Review review) {
        this.reviewCount++;
        this.ratingSum += review.getRating();
        this.ratingAvg = (float) ratingSum / this.reviewCount;
    }


    /**
     * 좋아요 개수 (true: 증가 / false: 감소)
     *
     * @param b boolean
     */
    public void updateLikeCount(boolean b) {
        if (b) {
            this.likeCount++;
        } else {
            if (this.likeCount > 0) {
                this.likeCount--;
            }
        }
    }

}
