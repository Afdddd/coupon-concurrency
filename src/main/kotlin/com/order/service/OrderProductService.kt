package com.order.service

import com.order.repository.OrderProductRepository
import com.product.repository.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderProductService(
    val orderProductRepository: OrderProductRepository,
    val productRepository: ProductRepository
) {

    @Transactional
    fun orderRollback(orderId: Long) {
        orderProductRepository.findByOrderId(orderId).forEach { orderProduct ->
            val product = productRepository.findByIdForUpdate(orderProduct.product.id!!)
                ?: throw IllegalStateException("Product not found")
            product.addStock(orderProduct.quantity)
        }
    }
}