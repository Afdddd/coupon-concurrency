package com.order.repository

import com.order.entity.OrderProduct
import org.springframework.data.jpa.repository.JpaRepository

interface OrderProductRepository: JpaRepository<OrderProduct, Long>