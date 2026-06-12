package com.order.service

import com.order.dto.OrderCreateRequest
import com.payment.PaymentClient
import org.springframework.stereotype.Service

@Service
class OrderFacade(
    val orderService: OrderService,
    val paymentClient: PaymentClient,
) {

    fun processOrder(userId: Long, orderRequest: OrderCreateRequest): Long {

        val orderId = orderService.createOrder(userId, orderRequest)

        if(paymentClient.pay(orderId)) {
            orderService.completeAsPaid(orderId)
        } else {
            orderService.cancelOrderAndRollbackStock(orderId)
        }
        return orderId
    }
}