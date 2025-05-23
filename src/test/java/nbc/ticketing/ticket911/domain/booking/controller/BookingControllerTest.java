package nbc.ticketing.ticket911.domain.booking.controller;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

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

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerTest {

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

	@BeforeEach
	void setUp() {
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
	void ConcurrencyProblem() throws InterruptedException {
		int threadCount = 300;
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);

		List<Future<String>> results = new ArrayList<>();

		for (int i = 0; i < threadCount; i++) {
			results.add(executor.submit(() -> {
				try {
					bookingService.createBookingByLettuce(authUser, bookingRequestDto);
					return "성공";
				} catch (Exception e) {
					return "실패: " + e.getMessage();
				} finally {
					latch.countDown();
				}
			}));
		}

		latch.await();

		int successCount = 0;
		int failureCount = 0;

		for (Future<String> result : results) {
			try {
				String outcome = result.get();
				if (outcome.equals("성공"))
					successCount++;
				else
					failureCount++;
				System.out.println(outcome);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out.println("성공한 요청 수: " + successCount);
		System.out.println("실패한 요청 수: " + failureCount);

		assertThat(successCount).isLessThanOrEqualTo(1);
	}

}
