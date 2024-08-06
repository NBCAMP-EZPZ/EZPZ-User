package com.sparta.ezpzuser.domain.coupon.service;

import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.common.lock.DistributedLock;
import com.sparta.ezpzuser.domain.coupon.dto.CouponResponseDto;
import com.sparta.ezpzuser.domain.coupon.dto.UserCouponResponseDto;
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
import static com.sparta.ezpzuser.common.util.PageUtil.validatePageableWithPage;

@Service
@RequiredArgsConstructor
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
    @DistributedLock(key = "'downloadCoupon-couponId-'.concat(#couponId)")
    public UserCouponResponseDto downloadCoupon(Long couponId, User user) {
        Coupon coupon = getCoupon(couponId);
        validateDuplicateCoupon(user, coupon);
        coupon.download();
        UserCoupon userCoupon = userCouponRepository.save(UserCoupon.of(user, coupon));
        return UserCouponResponseDto.of(userCoupon);
    }

    // 분산락 미적용 테스트 메서드
    @Transactional
    public UserCouponResponseDto downloadCouponWithoutLock(Long couponId, User user) {
        Coupon coupon = getCoupon(couponId);
        validateDuplicateCoupon(user, coupon);
        coupon.download();
        UserCoupon userCoupon = userCouponRepository.save(UserCoupon.of(user, coupon));
        return UserCouponResponseDto.of(userCoupon);
    }

    /**
     * 다운로드 가능한 쿠폰 목록 조회
     *
     * @param pageable Pageable 객체
     * @return 다운로드 가능한 쿠폰 목록
     */
    @Transactional(readOnly = true)
    public Page<CouponResponseDto> findAllDownloadableCoupons(Pageable pageable) {
        Page<Coupon> page = couponRepository.findByRemainingCountGreaterThan(0, pageable);
        validatePageableWithPage(pageable, page);
        return page.map(CouponResponseDto::of);
    }

    /**
     * 마이 쿠폰 목록 조회
     *
     * @param user     이용자 객체
     * @param pageable Pageable 객체
     * @return 마이 쿠폰 목록
     */
    @Transactional(readOnly = true)
    public Page<CouponResponseDto> findAllMyCoupons(User user, Pageable pageable) {
        Page<Coupon> page = userCouponRepository.findAllMyCoupons(user, pageable);
        validatePageableWithPage(pageable, page);
        return page.map(CouponResponseDto::of);
    }

    /* UTIL */

    private Coupon getCoupon(Long couponId) {
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new CustomException(COUPON_NOT_FOUND));
    }

    /**
     * 이미 다운로드 받은 쿠폰인지 확인
     *
     * @param user   이용자 객체
     * @param coupon 쿠폰 객체
     */
    private void validateDuplicateCoupon(User user, Coupon coupon) {
        if (userCouponRepository.existsByUserAndCoupon(user, coupon)) {
            throw new CustomException(ALREADY_DOWNLOADED_COUPON);
        }
    }

}
