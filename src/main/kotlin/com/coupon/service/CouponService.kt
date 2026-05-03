package com.coupon.service

import com.coupon.repository.CouponRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CouponService(
    private val couponRepository: CouponRepository,
    private val couponIssueService: CouponIssueService
) {

    @Transactional
    fun issueCoupon(couponId: Long, userId: Long): String {
        if (couponIssueService.isAlreadyIssued(couponId, userId)) {
            throw RuntimeException("이미 수령한 쿠폰입니다.")
        }

        val coupon = couponRepository.findByIdForUpdate(couponId)
            ?: throw RuntimeException("유효하지 않은 쿠폰입니다.")

        if (!coupon.isAvailable()) {
            throw RuntimeException("남은 쿠폰이 없습니다.")
        }

        coupon.reduceCount()
        couponIssueService.save(coupon, userId)
        return coupon.code
    }
}