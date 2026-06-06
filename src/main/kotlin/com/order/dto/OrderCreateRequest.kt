package com.order.dto

data class OrderCreateRequest(
    val items: List<OrderItemRequest>
)

data class OrderItemRequest(
    val productId: Long,
    val quantity: Int
)