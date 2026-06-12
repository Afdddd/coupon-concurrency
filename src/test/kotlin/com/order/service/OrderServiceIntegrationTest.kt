package com.order.service

import com.conf.TestcontainersConfiguration
import com.global.exception.OutOfStockException
import com.order.dto.OrderCreateRequest
import com.order.dto.OrderItemRequest
import com.order.entity.OrderStatus
import com.order.repository.OrderProductRepository
import com.order.repository.OrderRepository
import com.payment.FakePaymentClient
import com.payment.PaymentClient
import com.product.entity.Product
import com.product.repository.ProductRepository
import com.user.entity.User
import com.user.repository.UserRepository
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test

@SpringBootTest
@Import(TestcontainersConfiguration::class)
@Transactional
class OrderServiceIntegrationTest() {

    @Autowired lateinit var orderService: OrderIntegrationService
    @Autowired lateinit var productRepository: ProductRepository
    @Autowired lateinit var userRepository: UserRepository
    @Autowired lateinit var orderRepository: OrderRepository
    @Autowired lateinit var orderProductRepository: OrderProductRepository
    @Autowired lateinit var paymentClient: PaymentClient
    @Autowired lateinit var em: EntityManager

    lateinit var testProduct1: Product
    lateinit var testProduct2: Product
    lateinit var testUser: User

    @BeforeEach
    fun setUp() {
        testUser = userRepository.save(User())
        testProduct1 = productRepository.save(Product(name = "mac-mini", price = 100, stock = 10))
        testProduct2 = productRepository.save(Product(name = "mac-studio", price = 500, stock = 10))
        em.flush()
    }

    @Test
    fun `주문 생성시 재고가 차감되고 결제완료 상태가 된다`() {

        // given
        (paymentClient as FakePaymentClient).isPaymentSuccessful = true
        val request = OrderCreateRequest(
            listOf(
                OrderItemRequest(testProduct1.id!!, 2),
                OrderItemRequest(testProduct2.id!!, 1)
            )
        )

        // when
        val orderId = orderService.order(testUser.id!!, request)

        // then
        em.flush()
        em.clear()
        val order = orderRepository.findById(orderId).get()

        val orderProducts = orderProductRepository.findByOrderId(orderId)
        val product1 = orderProducts[0].product
        val product2 = orderProducts[1].product

        assertEquals(OrderStatus.PAID, order.orderStatus)
        assertEquals(700, order.totalPrice)
        assertEquals(order.user.id, testUser.id)
        assertEquals(8, product1.stock)
        assertEquals(9, product2.stock)
    }

    @Test
    fun `주문 생성시 재고가 부족할 경우 예외가 발생한다`() {

        // given
        val request = OrderCreateRequest(
            listOf(
                OrderItemRequest(testProduct1.id!!, 11)
            )
        )

        // when & then
        assertThrows(OutOfStockException::class.java) {
            orderService.order(testUser.id!!, request)
        }
    }
}