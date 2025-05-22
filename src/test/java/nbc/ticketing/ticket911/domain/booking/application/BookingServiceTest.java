package nbc.ticketing.ticket911.domain.booking.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import nbc.ticketing.ticket911.domain.auth.vo.AuthUser;
import nbc.ticketing.ticket911.domain.booking.dto.request.BookingRequestDto;
import nbc.ticketing.ticket911.domain.booking.exception.BookingException;
import nbc.ticketing.ticket911.domain.booking.service.BookingDomainService;
import nbc.ticketing.ticket911.domain.concert.entity.Concert;
import nbc.ticketing.ticket911.domain.concert.repository.ConcertRepository;
import nbc.ticketing.ticket911.domain.concertseat.entity.ConcertSeat;
import nbc.ticketing.ticket911.domain.concertseat.repository.ConcertSeatRepository;
import nbc.ticketing.ticket911.domain.concertseat.service.ConcertSeatDomainService;
import nbc.ticketing.ticket911.domain.seat.entity.Seat;
import nbc.ticketing.ticket911.domain.seat.repository.SeatRepository;
import nbc.ticketing.ticket911.domain.stage.entity.Stage;
import nbc.ticketing.ticket911.domain.stage.repository.StageRepository;
import nbc.ticketing.ticket911.domain.stage.status.StageStatus;
import nbc.ticketing.ticket911.domain.user.constant.UserRole;
import nbc.ticketing.ticket911.domain.user.entity.User;
import nbc.ticketing.ticket911.domain.user.repository.UserRepository;
import nbc.ticketing.ticket911.domain.user.service.UserDomainService;

@SpringBootTest
@ActiveProfiles("test")
class BookingServiceTest {
	@Autowired
	private BookingService bookingService;

	@Autowired
	private ConcertSeatDomainService concertSeatDomainService;

	@Autowired
	private BookingDomainService bookingDomainService;

	@MockBean
	private UserDomainService userDomainService;
	@Autowired
	private ConcertSeatRepository concertSeatRepository;
	@Autowired
	private ConcertRepository concertRepository;
	@Autowired
	private SeatRepository seatRepository;

	private Long savedConcertSeatId;
	@Autowired
	private StageRepository stageRepository;
	@Autowired
	private UserRepository userRepository;

	private User savedUser;

	@BeforeEach
	void setupConcertSeat() {
		savedUser = userRepository.save(
			User.builder()
				.email("user" + UUID.randomUUID() + "@email.com")
				.password("encoded")
				.nickname("tester")
				.point(10000)
				.roles(Set.of(UserRole.ROLE_USER))
				.isDeleted(false)
				.build()
		);

		when(userDomainService.findActiveUserById(savedUser.getId()))
			.thenReturn(savedUser);

		Stage savedStage = stageRepository.save(
			Stage.builder()
				.totalSeat(1L)
				.stageName("Main Stage")
				.stageStatus(StageStatus.AVAILABLE)
				.build()
		);

		Concert savedConcert = concertRepository.save(
			Concert.builder()
				.title("테스트 공연")
				.description("테스트 설명")
				.startTime(LocalDateTime.now().plusDays(1))
				.ticketOpen(LocalDateTime.now().minusDays(1))
				.ticketClose(LocalDateTime.now().plusHours(1))
				.stage(savedStage)
				.user(savedUser)
				.build()
		);

		Seat savedSeat = seatRepository.save(
			Seat.builder()
				.seatName("A-1")
				.seatPrice(0L)
				.stage(savedStage)
				.build()
		);

		ConcertSeat concertSeat = concertSeatRepository.save(ConcertSeat.create(savedConcert, savedSeat));
		savedConcertSeatId = concertSeat.getId(); // 테스트용으로 저장
	}

	@Test
	void 동시_예매_테스트_좌석은_하나만_성공해야_한다() throws InterruptedException {
		int threadCount = 100;
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);

		// 💡 저장된 유저의 정보를 가져옵니다
		User savedUser = userRepository.findAll().get(0); // 또는 별도로 savedUser 필드로 관리
		AuthUser authUser = AuthUser.of(savedUser.getId(), savedUser.getEmail(), savedUser.getRoles());
		BookingRequestDto dto = new BookingRequestDto(List.of(savedConcertSeatId));

		List<String> results = Collections.synchronizedList(new ArrayList<>());

		for (int i = 0; i < threadCount; i++) {
			executor.submit(() -> {
				try {
					bookingService.createBooking(authUser, dto);
					results.add("성공");
				} catch (BookingException e) {
					results.add(e.getErrorCode().name());
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		Map<String, Long> resultStats = results.stream()
			.collect(Collectors.groupingBy(r -> r, Collectors.counting()));

		resultStats.forEach((result, count) -> {
			System.out.println("[" + result + "] = " + count + "건");
		});

		assertThat(resultStats.getOrDefault("성공", 0L)).isEqualTo(1);
	}

}
