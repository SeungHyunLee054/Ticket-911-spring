package nbc.ticketing.ticket911.domain.lock;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import lombok.extern.slf4j.Slf4j;
import nbc.ticketing.ticket911.common.aop.RedissonMultiLockAspect;
import nbc.ticketing.ticket911.domain.auth.vo.AuthUser;
import nbc.ticketing.ticket911.domain.booking.application.BookingService;
import nbc.ticketing.ticket911.domain.booking.dto.request.BookingRequestDto;
import nbc.ticketing.ticket911.domain.concert.entity.Concert;
import nbc.ticketing.ticket911.domain.concert.repository.ConcertRepository;
import nbc.ticketing.ticket911.domain.concertseat.entity.ConcertSeat;
import nbc.ticketing.ticket911.domain.concertseat.repository.ConcertSeatRepository;
import nbc.ticketing.ticket911.domain.seat.entity.Seat;
import nbc.ticketing.ticket911.domain.seat.repository.SeatRepository;
import nbc.ticketing.ticket911.domain.stage.entity.Stage;
import nbc.ticketing.ticket911.domain.stage.repository.StageRepository;
import nbc.ticketing.ticket911.domain.stage.status.StageStatus;
import nbc.ticketing.ticket911.domain.user.constant.UserRole;
import nbc.ticketing.ticket911.domain.user.entity.User;
import nbc.ticketing.ticket911.domain.user.repository.UserRepository;

@Slf4j
@Testcontainers
@ActiveProfiles("test")
@SpringBootTest
@Import(RedissonMultiLockAspect.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class BookingConcurrencyTest {

	@Container
	static GenericContainer<?> redis =
		new GenericContainer<>(DockerImageName.parse("redis:7.0-alpine"))
			.withExposedPorts(6379)
	 		.waitingFor(Wait.forListeningPort())
			.withStartupAttempts(5);

	@DynamicPropertySource
	static void redisProperties(DynamicPropertyRegistry reg) {
		reg.add("spring.data.redis.host", redis::getHost);
		reg.add("spring.data.redis.port", redis::getFirstMappedPort);
	}

	@Autowired
	private BookingService bookingService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private StageRepository stageRepository;

	@Autowired
	private SeatRepository seatRepository;

	@Autowired
	private ConcertRepository concertRepository;

	@Autowired
	private ConcertSeatRepository concertSeatRepository;

	private ConcertSeat concertSeat;
	private BookingRequestDto bookingRequestDto;
	private AuthUser authUser;

	private final int THREAD_COUNT = 10;

	@BeforeEach
	void setUp() throws InterruptedException {
		System.out.println(">>> bookingService class = " + bookingService.getClass().getName());
		Thread.sleep(3000);
		User user = userRepository.save(User.builder()
			.email("test@example.com")
			.nickname("test")
			.point(100000000)
			.roles(Set.of(UserRole.ROLE_USER))
			.build());

		authUser = AuthUser.builder()
			.id(user.getId())
			.email(user.getEmail())
			.roles(Set.of(UserRole.ROLE_USER))
			.build();

		Stage stage = stageRepository.save(Stage.builder()
			.stageName("테스트홀")
			.stageStatus(StageStatus.AVAILABLE)
			.totalSeat(0L)
			.build());

		Seat seat = seatRepository.save(Seat.builder()
			.stage(stage)
			.seatName("A-1")
			.seatPrice(50L)
			.build());

		Concert concert = concertRepository.save(Concert.builder()
			.user(user)
			.stage(stage)
			.title("테스트 공연")
			.description("설명")
			.startTime(LocalDateTime.now().plusDays(1))
			.ticketOpen(LocalDateTime.now().minusDays(1))
			.ticketClose(LocalDateTime.now().plusDays(1))
			.isSoldOut(false)
			.build());

		concertSeat = concertSeatRepository.save(ConcertSeat.builder()
			.concert(concert)
			.seat(seat)
			.isReserved(false)
			.build());

		bookingRequestDto = new BookingRequestDto(List.of(concertSeat.getId()));
	}

	@Test
	void 동시에_같은_좌석_예매_요청이_들어오면_하나만_성공해야_한다() throws InterruptedException {
		ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
		CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
		AtomicInteger successCount = new AtomicInteger();
		AtomicInteger failCount = new AtomicInteger();

		for (int i = 0; i < THREAD_COUNT; i++) {
			executorService.execute(() -> {
				try {
					bookingService.createBookingByRedisson(authUser, bookingRequestDto);
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

		assertEquals(1, successCount.get(), "예매는 오직 한 명만 성공해야 합니다.");
		assertEquals(THREAD_COUNT - 1, failCount.get(), "나머지 쓰레드는 모두 실패해야 합니다.");
	}
}
