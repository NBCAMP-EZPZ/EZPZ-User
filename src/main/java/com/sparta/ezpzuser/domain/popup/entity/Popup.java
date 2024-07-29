package com.sparta.ezpzuser.domain.popup.entity;

import com.sparta.ezpzuser.common.entity.Timestamped;
import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.common.exception.ErrorType;
import com.sparta.ezpzuser.domain.host.entity.Host;
import com.sparta.ezpzuser.domain.popup.enums.ApprovalStatus;
import com.sparta.ezpzuser.domain.popup.enums.PopupStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @Column(name = "like_count")
    private int likeCount;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;
    
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
