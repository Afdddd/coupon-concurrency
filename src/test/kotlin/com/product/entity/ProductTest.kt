package com.product.entity

import com.global.exception.OutOfStockException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ProductTest {

    @Test
    fun `재고보다 적게 차감하면 재고가 줄어든다`() {
        // given
        val testProduct = Product(name = "mac-mini", price = 100, stock = 100)
        val input = 10

        // when
        testProduct.reduceStock(input)

        // then
        assertEquals(90, testProduct.stock)
    }

    @Test
    fun `재고보다 많은 요청이 들어오면 에러를 발생한다`() {
        // given
        val testProduct = Product(name = "mac-mini", price = 100, stock = 100)
        val input = 101

        assertThrows(OutOfStockException::class.java) {
            testProduct.reduceStock(input)
        }
    }

    @Test
    fun `음수로 요청하면 에러를 발생한다`() {
        // given
        val testProduct = Product(name = "mac-mini", price = 100, stock = 100)
        val input = -1

        assertThrows(IllegalArgumentException::class.java) {
            testProduct.reduceStock(input)
        }
    }

}