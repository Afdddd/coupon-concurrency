package com.coupon.entity

import com.order.entity.Order
import com.user.entity.User
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.JoinColumn
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import java.time.LocalDateTime

@Entity
class CouponIssue(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    val user: User,
    @ManyToOne(fetch = FetchType.LAZY)
    val coupon: Coupon,
    @OneToOne
    @JoinColumn(name = "order_id")
    val order: Order,
    val issueDate: LocalDateTime,
)