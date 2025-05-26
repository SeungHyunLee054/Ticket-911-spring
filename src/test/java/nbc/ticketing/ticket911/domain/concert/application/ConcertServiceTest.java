package nbc.ticketing.ticket911.domain.concert.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import nbc.ticketing.ticket911.domain.concert.dto.request.ConcertCreateRequest;
import nbc.ticketing.ticket911.domain.concert.dto.request.ConcertUpdateRequest;
import nbc.ticketing.ticket911.domain.concert.dto.response.ConcertCreateResponse;
import nbc.ticketing.ticket911.domain.concert.dto.response.ConcertDetailResponse;
import nbc.ticketing.ticket911.domain.concert.entity.Concert;
import nbc.ticketing.ticket911.domain.concert.repository.ConcertRepository;
import nbc.ticketing.ticket911.domain.concert.service.ConcertDomainService;
import nbc.ticketing.ticket911.domain.concertseat.application.ConcertSeatService;
import nbc.ticketing.ticket911.domain.stage.entity.Stage;
import nbc.ticketing.ticket911.domain.stage.service.StageDomainService;
import nbc.ticketing.ticket911.domain.user.entity.User;
import nbc.ticketing.ticket911.domain.user.service.UserDomainService;

@ExtendWith(MockitoExtension.class)
class ConcertServiceTest {

	@Mock private UserDomainService userDomainService;
	@Mock private StageDomainService stageDomainService;
	@Mock private ConcertDomainService concertDomainService;
	@Mock private ConcertRepository concertRepository;
	@Mock private ConcertSeatService concertSeatService;

	@InjectMocks private ConcertService concertService;

	private Long userId = 1L;
	private Long stageId = 2L;
	private Long concertId = 99L;
	private ConcertCreateRequest request;
	private User user;
	private Stage stage;
	private Concert concert;

	@BeforeEach
	void setUp() {
		request = new ConcertCreateRequest(
			stageId,
			"MyConcert",
			"test",
			LocalDateTime.of(2025, 6, 1, 19, 0),
			LocalDateTime.of(2025, 5, 1, 10, 0),
			LocalDateTime.of(2025, 5, 31, 18, 0)
		);

		user = User.builder().id(userId).nickname("tester").build();
		stage = Stage.builder().id(stageId).seats(List.of()).build();

		concert = Concert.builder()
			.id(concertId)
			.title("MyConcert")
			.description("설명")
			.startTime(request.getStartTime())
			.ticketOpen(request.getTicketOpen())
			.ticketClose(request.getTicketClose())
			.isSoldOut(false)
			.deletedAt(null)
			.user(user)
			.stage(stage)
			.build();
	}

	@Test
	void createConcert_success() {
		given(userDomainService.findActiveUserById(userId)).willReturn(user);
		given(stageDomainService.getStageWithSeatsById(stageId)).willReturn(stage);
		willDoNothing().given(concertDomainService)
			.validateCreatable(request.getStartTime(), request.getTicketOpen(), request.getTicketClose());
		given(concertDomainService.createConcert(user, stage, request)).willReturn(concert);

		ConcertCreateResponse response = concertService.createConcert(userId, stageId, request);

		then(userDomainService).should().findActiveUserById(userId);
		then(stageDomainService).should().getStageWithSeatsById(stageId);
		then(concertDomainService).should().validateCreatable(
			request.getStartTime(), request.getTicketOpen(), request.getTicketClose());
		then(concertDomainService).should().createConcert(user, stage, request);
		then(concertSeatService).should().createConcertSeats(concert, stage.getSeats());

		assertEquals(concert.getId(), response.getId());
		assertEquals(concert.getTitle(), response.getTitle());
	}

	@Test
	void createConcert_validationFails_shouldThrow() {
		given(userDomainService.findActiveUserById(userId)).willReturn(user);
		given(stageDomainService.getStageWithSeatsById(stageId)).willReturn(stage);
		willThrow(new IllegalArgumentException("invalid")).given(concertDomainService)
			.validateCreatable(any(), any(), any());

		IllegalArgumentException ex = assertThrows(
			IllegalArgumentException.class,
			() -> concertService.createConcert(userId, stageId, request)
		);
		assertEquals("invalid", ex.getMessage());

		then(concertSeatService).should(never()).createConcertSeats(any(), anyList());
	}

	@Test
	void getConcertDetail_success() {
		given(concertDomainService.getActiveConcertById(concertId)).willReturn(concert);

		ConcertDetailResponse resp = concertService.getConcertDetail(concertId);

		then(concertDomainService).should().getActiveConcertById(concertId);
		assertEquals(concert.getId(), resp.getId());
		assertEquals(concert.getTitle(), resp.getTitle());
	}

	@Test
	void getConcertDetail_notFound_shouldThrow() {
		given(concertDomainService.getActiveConcertById(concertId))
			.willThrow(new IllegalArgumentException("공연이 존재하지 않습니다."));

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
			concertService.getConcertDetail(concertId)
		);

		assertEquals("공연이 존재하지 않습니다.", ex.getMessage());
	}

	@Test
	void updateConcert_success() {
		ConcertUpdateRequest updateRequest = new ConcertUpdateRequest(
			"New Title",
			"New Desc",
			LocalDateTime.of(2025, 6, 10, 20, 0),
			LocalDateTime.of(2025, 5, 2, 9, 0),
			LocalDateTime.of(2025, 5, 30, 23, 0)
		);

		given(concertDomainService.getConcertById(concertId)).willReturn(concert);
		willDoNothing().given(concertDomainService)
			.validateCreatable(updateRequest.getStartTime(), updateRequest.getTicketOpen(), updateRequest.getTicketClose());
		willDoNothing().given(concertDomainService).validateUpdatable(concert, userId);

		ConcertDetailResponse response = concertService.updateConcert(concertId, userId, updateRequest);

		then(concertDomainService).should().getConcertById(concertId);
		then(concertDomainService).should().validateCreatable(
			updateRequest.getStartTime(), updateRequest.getTicketOpen(), updateRequest.getTicketClose());
		then(concertDomainService).should().validateUpdatable(concert, userId);

		assertEquals(updateRequest.getTitle(), concert.getTitle());
		assertEquals(updateRequest.getDescription(), concert.getDescription());
		assertEquals(updateRequest.getStartTime(), concert.getStartTime());
		assertEquals(updateRequest.getTicketOpen(), concert.getTicketOpen());
		assertEquals(updateRequest.getTicketClose(), concert.getTicketClose());
		assertEquals(concert.getId(), response.getId());
	}

	@Test
	void updateConcert_validationFails_shouldThrow() {
		ConcertUpdateRequest updateRequest = new ConcertUpdateRequest(
			"Bad Title",
			"Bad Desc",
			LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now()
		);

		given(concertDomainService.getConcertById(concertId)).willReturn(concert);
		willThrow(new IllegalArgumentException("유효하지 않은 값입니다.")).given(concertDomainService)
			.validateCreatable(any(), any(), any());

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
			concertService.updateConcert(concertId, userId, updateRequest)
		);

		assertEquals("유효하지 않은 값입니다.", ex.getMessage());
	}

	@Test
	void deleteConcert_success() {
		given(concertDomainService.getConcertById(concertId)).willReturn(concert);
		willDoNothing().given(concertDomainService).validateDeletable(concert, userId);

		assertDoesNotThrow(() -> concertService.deleteConcert(concertId, userId));

		then(concertDomainService).should().getConcertById(concertId);
		then(concertDomainService).should().validateDeletable(concert, userId);
	}

	@Test
	void deleteConcert_invalidUser_shouldThrow() {
		given(concertDomainService.getConcertById(concertId)).willReturn(concert);
		willThrow(new IllegalArgumentException("삭제 권한이 없습니다.")).given(concertDomainService)
			.validateDeletable(concert, userId);

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
			concertService.deleteConcert(concertId, userId)
		);

		assertEquals("삭제 권한이 없습니다.", ex.getMessage());
	}
}
