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
        check(this.orderStatus == OrderStatus.READY) { "결제 대기가 아닌 경우 결제 완료로 변경이 불가능합니다." }
        this.orderStatus = OrderStatus.PAID
    }
    fun failed() {
        check(this.orderStatus == OrderStatus.READY) { "결제 대가가 아닌 경우 실패로 변경이 불가능합니다." }
        this.orderStatus = OrderStatus.FAILED
    }
    fun updateTotalPrice(totalPrice: Int) {
        if(totalPrice < 0) throw IllegalArgumentException("Total price는 음수가 될 수 없습니다. $totalPrice")
        this.totalPrice = totalPrice
    }
}