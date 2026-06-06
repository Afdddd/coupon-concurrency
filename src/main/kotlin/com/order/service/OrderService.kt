package com.order.service

import com.order.dto.OrderCreateRequest
import com.order.entity.Order
import com.order.entity.OrderProduct
import com.order.entity.OrderStatus
import com.order.repository.OrderProductRepository
import com.order.repository.OrderRepository
import com.payment.PaymentClient
import com.product.repository.ProductRepository
import com.user.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    val orderRepository: OrderRepository,
    val userRepository: UserRepository,
    val productRepository: ProductRepository,
    val orderProductRepository: OrderProductRepository,
    val paymentClient: PaymentClient
) {

    @Transactional
    fun createOrder(userId: Long, orderRequest: OrderCreateRequest): Long {
        var totalPrice = 0

        val user = userRepository.findByIdOrNull(userId)
            ?: throw EntityNotFoundException("User not found")

        val order = orderRepository.save(
            Order(
                user = user,
                orderStatus = OrderStatus.READY
            )
        )

        orderRequest.items.forEach { item ->
            val product = productRepository.findByIdOrNull(item.productId)
                ?: throw EntityNotFoundException("Product not found")
            product.reduceStock(item.quantity)
            totalPrice += product.price * item.quantity

            orderProductRepository.save(OrderProduct(order = order, product = product, quantity = item.quantity))
        }

        order.updateTotalPrice(totalPrice)

        if(paymentClient.pay(order)) {
            order.paid()
        } else {
            throw Exception("Payment failed")
        }

        return order.id!!
    }
}