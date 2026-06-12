package com.order.service

import com.order.dto.OrderCreateRequest
import com.order.entity.Order
import com.order.entity.OrderProduct
import com.order.entity.OrderStatus
import com.order.repository.OrderProductRepository
import com.order.repository.OrderRepository
import com.product.repository.ProductRepository
import com.product.service.ProductStockService
import com.user.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    val productStockService: ProductStockService,
    val orderRepository: OrderRepository,
    val userRepository: UserRepository,
) {

    @Transactional
    fun createOrder(userId: Long, orderRequest: OrderCreateRequest): Long {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw EntityNotFoundException("User not found")

        val order = orderRepository.save(Order(user = user, orderStatus = OrderStatus.READY))
        val calculatedAmount = productStockService.reserveStock(order, orderRequest.items)
        order.updateTotalPrice(calculatedAmount)
        return order.id!!
    }

    @Transactional
    fun completeAsPaid(orderId: Long) {
        val order = orderRepository.findByIdOrNull(orderId)
            ?: throw EntityNotFoundException("Order not found")
        order.paid()
    }

    @Transactional
    fun cancelOrderAndRollbackStock(orderId: Long) {
        productStockService.rollbackStock(orderId)
        completeAsFailed(orderId)
    }

    private fun completeAsFailed(orderId: Long) {
        val order = orderRepository.findByIdOrNull(orderId)
            ?: throw EntityNotFoundException("Order not found")
        order.failed()
    }

}