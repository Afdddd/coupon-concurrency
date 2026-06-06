package com.order.entity

import com.user.entity.User
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "orders")
class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    val user: User,
    var totalPrice: Int = 0,
    @Enumerated(EnumType.STRING)
    var orderStatus: OrderStatus,
) {
    fun paid() {
        this.orderStatus = OrderStatus.PAID
    }
    fun ready() {
        this.orderStatus = OrderStatus.READY
    }
    fun canceled() {
        this.orderStatus = OrderStatus.CANCELED
    }
    fun updateTotalPrice(totalPrice: Int) {
        this.totalPrice = totalPrice
    }
}