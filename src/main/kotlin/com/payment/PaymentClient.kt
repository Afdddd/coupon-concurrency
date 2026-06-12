package com.payment


interface PaymentClient {
    fun pay(orderId: Long): Boolean
}