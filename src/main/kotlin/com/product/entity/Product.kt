package com.product.entity

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
    private fun isAvailable(stock: Int): Boolean {
        if(this.stock - stock < 0)
            throw Exception("Product is out of stock")
        return true
    }

    fun reduceStock(stock: Int) {
        if(this.isAvailable(stock)) {
            this.stock -= stock
        } else {
            throw Exception("Product is out of stock")
        }
    }
}