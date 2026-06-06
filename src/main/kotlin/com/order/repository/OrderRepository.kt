package com.order.repository

import com.order.entity.Order
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository: JpaRepository<Order, Long>