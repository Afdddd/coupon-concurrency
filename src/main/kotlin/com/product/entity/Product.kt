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
    private fun validateStock(stock: Int) {
        if(this.stock - stock < 0) {
            throw OutOfStockException()
        }
    }

    fun reduceStock(stock: Int) {
        validateStock(stock)
        this.stock -= stock
    }

    fun addStock(stock: Int) {
        this.stock += stock
    }
}