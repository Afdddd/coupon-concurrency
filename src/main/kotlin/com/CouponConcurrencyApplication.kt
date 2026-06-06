package com

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CouponConcurrencyApplication

fun main(args: Array<String>) {
    runApplication<CouponConcurrencyApplication>(*args)
}