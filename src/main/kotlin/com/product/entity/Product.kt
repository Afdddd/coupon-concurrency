package com.product.entity

import com.global.exception.OutOfStockException
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val name: String,
    val price: Int,
    var stock: Int,
) {
    fun reduceStock(stock: Int) {
        validateRequestStock(stock)
        if(this.stock - stock < 0) {
            throw OutOfStockException()
        }
        this.stock -= stock
    }

    fun addStock(stock: Int) {
        validateRequestStock(stock)
        this.stock += stock
    }

    private fun validateRequestStock(stock: Int) {
        require(stock > 0) {"재고 요청은 0보다 커야 합니다. $stock"}
    }
}