package com.order.entity

import com.product.entity.Product
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne

@Entity
class OrderProduct(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    val order: Order,
    @ManyToOne(fetch = FetchType.LAZY)
    val product: Product,
    val quantity: Int = 0
) {
    fun totalPrice(): Int = product.price * quantity
}