package com.coupon.repository

import com.coupon.entity.CouponIssue
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CouponIssueRepository: JpaRepository<CouponIssue, Long> {
    @Query(
        """
            SELECT COUNT(ci) > 0 
            FROM CouponIssue ci
            WHERE ci.userId = :userId 
            AND ci.coupon.id = :couponId
        """
    )
    fun existByCouponIdAndUserId(couponId: Long, userId: Long): Boolean
}