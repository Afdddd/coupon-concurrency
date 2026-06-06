package com.order.entity

enum class OrderStatus(val label: String) {
    READY("결제대기"),
    PAID("결제완료"),
    CANCELED("취소됨");
}