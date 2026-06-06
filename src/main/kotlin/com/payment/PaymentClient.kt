package com.payment

import com.order.entity.Order

interface PaymentClient {
    fun pay(order: Order): Boolean
}