package com.payment

import org.springframework.stereotype.Component

@Component
class FakePaymentClient(
    success: Boolean = false
) : PaymentClient {

    var isPaymentSuccessful: Boolean = success

    override fun pay(orderId: Long): Boolean {
        try {
            Thread.sleep(1000)
            return isPaymentSuccessful
        } catch (e: Exception) {
            return isPaymentSuccessful
        }
    }
}