package com.coupon.repository

import com.coupon.entity.Coupon
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface CouponRepository: JpaRepository<Coupon, Long> {

    @Modifying
    @Query("""
       UPDATE Coupon c 
       SET c.count = c.count - 1 
       WHERE c.id = :couponId
       AND c.count > 0
    """)
    fun decreaseCountIfAvailable(couponId: Long): Int

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Coupon c WHERE c.id = :couponId")
    fun findByIdForUpdate(couponId: Long): Coupon?
}