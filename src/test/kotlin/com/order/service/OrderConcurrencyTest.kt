package com.order.service

import com.conf.TestcontainersConfiguration
import com.order.dto.OrderCreateRequest
import com.order.dto.OrderItemRequest
import com.payment.FakePaymentClient
import com.payment.PaymentClient
import com.product.entity.Product
import com.product.repository.ProductRepository
import com.user.entity.User
import com.user.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.dao.CannotAcquireLockException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest
@Import(TestcontainersConfiguration::class)
class OrderConcurrencyTest {

    @Autowired lateinit var orderService: OrderFacade
    @Autowired lateinit var productRepository: ProductRepository
    @Autowired lateinit var userRepository: UserRepository
    @Autowired lateinit var paymentClient: PaymentClient
    var userId: Long = 0
    var productId1: Long = 0
    var productId2: Long = 0


    @BeforeEach
    fun setUp() {
        userId = userRepository.save(User()).id!!
        productId1 = productRepository.save(Product(name = "mac-mini", price = 100, stock = 100)).id!!
        productId2 = productRepository.save(Product(name = "mac-book", price = 120, stock = 100)).id!!
    }

    @Test
    fun `동시에 100개 주문하면 재고가 0이다`() {
        // given
        (paymentClient as FakePaymentClient).isPaymentSuccessful = true
        val threadCount = 100
        val executor = Executors.newFixedThreadPool(threadCount)
        val readLatch = CountDownLatch(1)
        val doneLatch = CountDownLatch(threadCount)

        // when
        repeat(threadCount) {
            executor.submit {
                try {
                    readLatch.await()
                    orderService.processOrder(userId, OrderCreateRequest(listOf(OrderItemRequest(productId1, 1))))
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
        val product = productRepository.findById(productId1).get()

        assertEquals(0, product.stock)
    }

    @Test
    fun `상품 id를 정렬하면 데드락이 발생하지 않는다`() {
        // given
        (paymentClient as FakePaymentClient).isPaymentSuccessful = true
        val threadCount = 100
        val executor = Executors.newFixedThreadPool(threadCount)
        val readLatch = CountDownLatch(1)
        val doneLatch = CountDownLatch(threadCount)
        val count = AtomicInteger(0)

        val forwardOrder = listOf(OrderItemRequest(productId1, 1), OrderItemRequest(productId2, 1)) // 상품1 → 상품2
        val reverseOrder = listOf(OrderItemRequest(productId2, 1), OrderItemRequest(productId1, 1)) // 상품2 → 상품1

        // when
        repeat(threadCount / 2) {
            executor.submit {
                try {
                    readLatch.await()
                    orderService.processOrder(userId, OrderCreateRequest(forwardOrder))
                } catch (_: CannotAcquireLockException) {
                    count.incrementAndGet()
                } finally {
                    doneLatch.countDown()
                }
            }
        }

        repeat(threadCount / 2) {
            executor.submit {
                try {
                    readLatch.await()
                    orderService.processOrder(userId, OrderCreateRequest(reverseOrder))
                } catch (_: CannotAcquireLockException) {
                    count.incrementAndGet()
                } finally {
                    doneLatch.countDown()
                }
            }
        }
        readLatch.countDown()
        doneLatch.await()
        executor.shutdown()

        // then
        assertEquals(0, count.get())
    }

}