package com.sparta.ezpzuser.domain.coupon.entity;

import com.sparta.ezpzuser.common.exception.CustomException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static com.sparta.ezpzuser.common.exception.ErrorType.SOLD_OUT_COUPON;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Long id;

    private String name;

    private int discountAmount;

    private int totalCount; // 총 발행 개수

    private int remainingCount; // 남은 수량

    private LocalDate expiredAt;

    private Coupon(int totalCount) {
        this.totalCount = totalCount;
        this.remainingCount = totalCount;
    }

    public static Coupon of(int totalCount) {
        return new Coupon(totalCount);
    }

    /**
     * 쿠폰 다운로드
     */
    public void download() {
        // 남은 수량이 있는지 확인
        if (this.remainingCount == 0) {
            throw new CustomException(SOLD_OUT_COUPON);
        }
        // 남은 수량 감소
        remainingCount--;
    }

}
