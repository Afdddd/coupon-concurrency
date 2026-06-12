package com.order.entity

import com.user.entity.User
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

class OrderTest {

    lateinit var testUser: User

    @BeforeEach
    fun setUp() {
        testUser = User()
    }

    @Test
    fun `결제 대기일 경우 결제 완료로 변경`() {
        // given
        val testOrder = Order(user = testUser, orderStatus = OrderStatus.READY)
        val requestStatus = OrderStatus.PAID

        // when
        testOrder.paid()

        // then
        assertEquals(requestStatus, testOrder.orderStatus)
    }

    @Test
    fun `결제 대기가 아닐 경우 결제 완료로 변경이 불가능`() {
        // given
        val testOrder = Order(user = testUser, orderStatus = OrderStatus.PAID)
        val requestStatus = OrderStatus.PAID

        // when & then
        assertThrows(IllegalStateException::class.java) {
            testOrder.paid()
        }
    }

    @Test
    fun `결제 대기일 경우 실패로 변경이 가능`() {
        // given
        val testOrder = Order(user = testUser, orderStatus = OrderStatus.READY)
        val requestStatus = OrderStatus.FAILED

        // when
        testOrder.failed()

        // then
        assertEquals(requestStatus, testOrder.orderStatus)
    }

    @Test
    fun `결제 대기가 아닐 경우 실패로 변경이 불가능`() {
        // given
        val testOrder = Order(user = testUser, orderStatus = OrderStatus.PAID)
        val requestStatus = OrderStatus.FAILED

        // when & then
        assertThrows(IllegalStateException::class.java) {
            testOrder.failed()
        }
    }

    @Test
    fun `totalPrice가 음수가 될 수 없다`() {
        // given
        val testOrder = Order(user = testUser, orderStatus = OrderStatus.READY)
        val requestPrice = -1

        // when & then
        assertThrows(IllegalArgumentException::class.java) {
            testOrder.updateTotalPrice(requestPrice)
        }
    }


}