package nbc.ticketing.ticket911.domain.lock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import nbc.ticketing.ticket911.domain.auth.vo.AuthUser;
import nbc.ticketing.ticket911.domain.booking.application.BookingService;
import nbc.ticketing.ticket911.domain.booking.dto.request.BookingRequestDto;

@SpringBootTest
public class BookingConcurrencyTest {

	@Autowired
	private BookingService bookingService;

	private final ExecutorService executorService = Executors.newFixedThreadPool(10);
	private final int THREAD_COUNT = 10;

	@Test
	void 동시에_좌석_예매를_시도하면_한_명만_성공한다() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
		AtomicInteger successCount = new AtomicInteger();
		AtomicInteger failCount = new AtomicInteger();

		AuthUser authUser = AuthUser.builder()
			.id(1L)
			.build();

		BookingRequestDto request = new BookingRequestDto(List.of(5L, 6L));

		for (int i = 0; i < THREAD_COUNT; i++) {
			executorService.submit(() -> {
				try {
					bookingService.createBooking(authUser, request);
					System.out.println(Thread.currentThread().getName() + ": 예매 성공");
					successCount.incrementAndGet();
				} catch (Exception e) {
					System.out.println(Thread.currentThread().getName() + ": 예매 실패 - " + e.getMessage());
					failCount.incrementAndGet();
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		System.out.println("예매 성공 수: " + successCount.get());
		System.out.println("예매 실패 수: " + failCount.get());

		assertEquals(1, successCount.get()); // 한 명만 성공
		assertEquals(THREAD_COUNT - 1, failCount.get()); // 나머지는 실패
	}
}
