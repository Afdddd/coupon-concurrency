package com.coupon.service

import com.coupon.entity.Coupon
import com.coupon.entity.CouponIssue
import com.coupon.repository.CouponIssueRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CouponIssueService(
    private val couponIssueRepository: CouponIssueRepository
) {
    fun isAlreadyIssued(couponId: Long, userId: Long): Boolean =
        couponIssueRepository.existByCouponIdAndUserId(couponId, userId)

    @Transactional
    fun save(coupon: Coupon, userId: Long) {
        couponIssueRepository.save(CouponIssue(coupon = coupon, userId = userId))
    }
}