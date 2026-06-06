package com.payment

import com.order.entity.Order
import org.springframework.stereotype.Component

@Component
class FakePaymentClient: PaymentClient {
    override fun pay(order: Order): Boolean {
        return true
    }
}