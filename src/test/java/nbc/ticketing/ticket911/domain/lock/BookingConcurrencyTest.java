package nbc.ticketing.ticket911.domain.lock;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import lombok.extern.slf4j.Slf4j;
import nbc.ticketing.ticket911.domain.auth.vo.AuthUser;
import nbc.ticketing.ticket911.domain.booking.application.BookingService;
import nbc.ticketing.ticket911.domain.booking.dto.request.BookingRequestDto;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class BookingConcurrencyTest {

	@Autowired
	private BookingService bookingService;

	private final int THREAD_COUNT = 10;

	@Test
	void 동시에_같은_좌석_예매_요청이_들어오면_하나만_성공해야_한다() throws InterruptedException {
		ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
		CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
		AtomicInteger successCount = new AtomicInteger();
		AtomicInteger failCount = new AtomicInteger();

		List<Long> seatIds = List.of(5L, 6L);

		for (int i = 0; i < THREAD_COUNT; i++) {
			final long userId = i + 1;

			executorService.submit(() -> {
				AuthUser authUser = AuthUser.builder()
					.id(userId)
					.build();

				BookingRequestDto request = new BookingRequestDto(seatIds);

				try {
					bookingService.createBooking(authUser, request);
					log.info("[{}] 예매 성공", Thread.currentThread().getName());
					successCount.incrementAndGet();
				} catch (Exception e) {
					log.info("[{}] 예매 실패: {}", Thread.currentThread().getName(), e.getMessage());
					failCount.incrementAndGet();
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();
		executorService.shutdown();

		log.info("총 성공 횟수: {}", successCount.get());
		log.info("총 실패 횟수: {}", failCount.get());

		assertEquals(1, successCount.get(), "예매는 오직 한 명만 성공해야 합니다.");
		assertEquals(THREAD_COUNT - 1, failCount.get(), "나머지 쓰레드는 모두 실패해야 합니다.");
	}
}
