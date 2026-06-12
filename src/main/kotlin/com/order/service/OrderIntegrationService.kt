package com.order.service

import com.order.dto.OrderCreateRequest
import com.payment.PaymentClient
import org.springframework.stereotype.Service

@Service
class OrderIntegrationService(
    val orderService: OrderService,
    val paymentClient: PaymentClient,
    val orderProductService: OrderProductService
) {

    fun order(userId: Long, orderRequest: OrderCreateRequest): Long {

        val orderId = orderService.createOrder(userId, orderRequest)

        if(paymentClient.pay(orderId)) {
            orderService.orderPaid(orderId)
        } else {
            orderService.orderFailed(orderId)
            orderProductService.orderRollback(orderId)
        }

        return orderId
    }
}