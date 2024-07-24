package com.sparta.ezpzuser.domain.coupon.service;

import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.domain.coupon.entity.Coupon;
import com.sparta.ezpzuser.domain.coupon.entity.UserCoupon;
import com.sparta.ezpzuser.domain.coupon.repository.CouponRepository;
import com.sparta.ezpzuser.domain.coupon.repository.UserCouponRepository;
import com.sparta.ezpzuser.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sparta.ezpzuser.common.exception.ErrorType.ALREADY_DOWNLOADED_COUPON;
import static com.sparta.ezpzuser.common.exception.ErrorType.COUPON_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    /**
     * 쿠폰 다운로드
     *
     * @param couponId 다운로드할 쿠폰 id
     * @param user     다운로드 요청한 이용자 객체
     * @return 다운로드 후 생성된 UserCoupon 객체
     */
    @Transactional
    public UserCoupon downloadCoupon(Long couponId, User user) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CustomException(COUPON_NOT_FOUND));

        // 이미 다운로드 받은 쿠폰인지 확인
        if (userCouponRepository.existsByUserAndCoupon(user, coupon)) {
            throw new CustomException(ALREADY_DOWNLOADED_COUPON);
        }
        coupon.download();
        return userCouponRepository.save(UserCoupon.of(user, coupon));
    }

    /**
     * 다운로드 가능한 쿠폰 목록 조회
     *
     * @param pageable Pageable 객체
     * @return 다운로드 가능한 쿠폰 목록
     */
    public Page<Coupon> findAllDownloadableCoupons(Pageable pageable) {
        return couponRepository.findByRemainingCountGreaterThan(0, pageable);
    }

    /**
     * 마이 쿠폰 목록 조회
     *
     * @param user     이용자 객체
     * @param pageable Pageable 객체
     * @return 마이 쿠폰 목록
     */
    public Page<Coupon> findAllMyCoupons(User user, Pageable pageable) {
        return userCouponRepository.findAllMyCoupons(user, pageable);
    }

}
