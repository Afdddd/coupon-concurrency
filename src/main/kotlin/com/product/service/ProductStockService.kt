package com.product.service

import com.order.dto.OrderItemRequest
import com.order.entity.Order
import com.order.entity.OrderProduct
import com.order.repository.OrderProductRepository
import com.product.repository.ProductRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductStockService(
    val orderProductRepository: OrderProductRepository,
    val productRepository: ProductRepository,
) {

    @Transactional
    fun reserveStock(order: Order, items: List<OrderItemRequest>): Int {
        var totalPrice = 0
        items.sortedBy { it.productId }.forEach { item ->
            val product = productRepository.findByIdForUpdate(item.productId)
                ?: throw EntityNotFoundException("Product not found")
            product.reduceStock(item.quantity)

            val orderProduct = OrderProduct(order = order, product = product, quantity = item.quantity)
            totalPrice += orderProduct.totalPrice()

            orderProductRepository.save(orderProduct)
        }
        return totalPrice
    }

    @Transactional
    fun rollbackStock(orderId: Long) {
        orderProductRepository.findByOrderId(orderId).sortedBy { it.product.id }.forEach { orderProduct ->
            val product = productRepository.findByIdForUpdate(orderProduct.product.id!!)
                ?: throw IllegalStateException("Product not found")
            product.addStock(orderProduct.quantity)
        }
    }
}