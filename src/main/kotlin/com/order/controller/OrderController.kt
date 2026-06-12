package com.order.controller

import com.order.dto.OrderCreateRequest
import com.order.service.OrderIntegrationService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class OrderController(
    val orderService: OrderIntegrationService
) {

    @PostMapping
    fun createOrder(@RequestParam userId: Long, @RequestBody order: OrderCreateRequest): Long =
        orderService.order(userId, order)

}