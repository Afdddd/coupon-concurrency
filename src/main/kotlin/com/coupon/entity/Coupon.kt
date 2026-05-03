package com.coupon.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Version

@Entity
class Coupon(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val name: String,
    val code: String,
    var count: Int = 0,

    @Version
    var version: Long = 0
) {
    // 동시성
    fun reduceCount() {
        this.count -= 1
    }

    // 동시성
    fun isAvailable(): Boolean {
        return this.count > 0
    }
}