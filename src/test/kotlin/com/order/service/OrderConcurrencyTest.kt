package com.order.service

import com.conf.TestcontainersConfiguration
import com.order.dto.OrderCreateRequest
import com.order.dto.OrderItemRequest
import com.product.entity.Product
import com.product.repository.ProductRepository
import com.user.entity.User
import com.user.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest
@Import(TestcontainersConfiguration::class)
class OrderConcurrencyTest {

    @Autowired lateinit var orderService: OrderService
    @Autowired lateinit var productRepository: ProductRepository
    @Autowired lateinit var userRepository: UserRepository
    var userId: Long = 0
    var productId: Long = 0

    @BeforeEach
    fun setUp() {
        userId = userRepository.save(User()).id!!
        productId = productRepository.save(Product(name = "mac-mini", price = 100, stock = 100)).id!!
    }

    @Test
    fun `동시에 100개 주문하면 재고가 0이다`() {
        // given
        val threadCount = 100
        val executor = Executors.newFixedThreadPool(threadCount)
        val readLatch = CountDownLatch(1)
        val doneLatch = CountDownLatch(threadCount)

        // when
        repeat(threadCount) {
            executor.submit {
                try {
                    readLatch.await()
                    orderService.createOrder(userId, OrderCreateRequest(listOf(OrderItemRequest(productId, 1))))
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    doneLatch.countDown()
                }
            }
        }
        readLatch.countDown()
        doneLatch.await()

        // then
        val product = productRepository.findById(productId).get()

        assertEquals(0, product.stock)
    }


}