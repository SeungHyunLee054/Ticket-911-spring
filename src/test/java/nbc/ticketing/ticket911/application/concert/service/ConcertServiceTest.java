package nbc.ticketing.ticket911.application.concert.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import nbc.ticketing.ticket911.domain.concert.application.ConcertService;
import nbc.ticketing.ticket911.domain.concert.dto.request.ConcertCreateRequest;
import nbc.ticketing.ticket911.domain.concert.dto.request.ConcertSearchCondition;
import nbc.ticketing.ticket911.domain.concert.dto.request.ConcertUpdateRequest;
import nbc.ticketing.ticket911.domain.concert.dto.response.ConcertCreateResponse;
import nbc.ticketing.ticket911.domain.concert.dto.response.ConcertDetailResponse;
import nbc.ticketing.ticket911.domain.concert.dto.response.ConcertPageResponse;
import nbc.ticketing.ticket911.domain.concert.entity.Concert;
import nbc.ticketing.ticket911.domain.concert.repository.ConcertRepository;
import nbc.ticketing.ticket911.domain.concert.service.ConcertDomainService;
import nbc.ticketing.ticket911.domain.stage.entity.Stage;
import nbc.ticketing.ticket911.domain.stage.repository.StageRepository;
import nbc.ticketing.ticket911.domain.user.entity.User;
import nbc.ticketing.ticket911.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ConcertServiceTest {

	@InjectMocks
	private ConcertService concertService;

	@Mock
	private ConcertRepository concertRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private StageRepository stageRepository;
	@Mock
	private ConcertDomainService concertDomainService;

	@Test
	void createConcert_성공() {
		// given
		Long userId = 1L;
		Long stageId = 1L;
		LocalDateTime now = LocalDateTime.now();

		ConcertCreateRequest request = new ConcertCreateRequest(
			stageId,
			"테스트 콘서트",
			"테스트 설명입니다.",
			now.plusDays(10),
			now.plusDays(1),
			now.plusDays(5)
		);

		User user = mock(User.class);
		Stage stage = mock(Stage.class);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(stageRepository.findById(stageId)).thenReturn(Optional.of(stage));

		// when
		ConcertCreateResponse response = concertService.createConcert(userId, stageId, request);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo(request.getTitle());

		verify(concertDomainService).validateCreatable(
			request.getStartTime(), request.getTicketOpen(), request.getTicketClose());
		verify(concertRepository).save(any(Concert.class));
	}

	@Test
	void getConcertDetail_삭제되지않은_공연_조회() {
		// give
		Stage stage = mock(Stage.class);
		when(stage.getStageName()).thenReturn("예술의전당");

		Concert concert = mock(Concert.class);
		when(concert.getDeletedAt()).thenReturn(null);
		when(concert.getStage()).thenReturn(stage);

		when(concertRepository.findById(anyLong())).thenReturn(Optional.of(concert));

		// when
		ConcertDetailResponse response = concertService.getConcertDetail(1L);

		// then
		assertThat(response).isNotNull();
	}

	@Test
	void updateConcert_성공() {
		// given
		Long concertId = 1L;
		Long userId = 2L;
		LocalDateTime now = LocalDateTime.now();

		Concert concert = mock(Concert.class);
		Stage stage = mock(Stage.class);
		when(stage.getStageName()).thenReturn("예술의전당");
		when(concert.getStage()).thenReturn(stage);
		when(concert.getTitle()).thenReturn("테스트 콘서트");
		when(concert.getStartTime()).thenReturn(now.plusDays(10));
		when(concert.getDescription()).thenReturn("테스트 설명");

		ConcertUpdateRequest request = new ConcertUpdateRequest(
			"테스트 콘서트",
			"테스트 설명입니다.",
			now.plusDays(10),
			now.plusDays(1),
			now.plusDays(5)
		);

		when(concertRepository.findById(concertId)).thenReturn(Optional.of(concert));
		when(concert.getDescription()).thenReturn(request.getDescription());
		when(concert.getTicketOpen()).thenReturn(request.getTicketOpen());
		when(concert.getTicketClose()).thenReturn(request.getTicketClose());
		when(concert.getStartTime()).thenReturn(request.getStartTime());

		// when
		ConcertDetailResponse result = concertService.updateConcert(concertId, userId, request);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getTitle()).isEqualTo(request.getTitle());
		assertThat(result.getDescription()).isEqualTo(request.getDescription());
		assertThat(result.getStartTime()).isEqualTo(request.getStartTime());
		assertThat(result.getTicketOpen()).isEqualTo(request.getTicketOpen());
		assertThat(result.getTicketClose()).isEqualTo(request.getTicketClose());

		verify(concertDomainService).validateUpdatable(concert, userId);
		verify(concert).update(request);
	}

	@Test
	void deleteConcert_성공() {
		// given
		Long concertId = 1L;
		Long userId = 1L;
		Concert concert = mock(Concert.class);
		when(concertRepository.findById(concertId)).thenReturn(Optional.of(concert));

		// when
		concertService.deleteConcert(concertId, userId);

		// then
		verify(concertDomainService).validateDeletable(concert, userId);
		verify(concert).delete();
	}

	@Test
	void searchConcerts_조건검색_성공() {
		ConcertSearchCondition condition = new ConcertSearchCondition(
			"테스트",
			LocalDateTime.now().minusDays(1),
			LocalDateTime.now().plusDays(10),
			"예술의전당"
		);

		ConcertPageResponse dto = new ConcertPageResponse(1L, "테스트 콘서트", "예술의전당", "예술의 전당", LocalDateTime.now().plusDays(10),LocalDateTime.now().plusDays(1),LocalDateTime.now().plusDays(5));
		Page<ConcertPageResponse> mockPage = new PageImpl<>(List.of(dto));

		when(concertRepository.searchConcerts(any(), any())).thenReturn(mockPage);

		Page<ConcertPageResponse> result = concertService.searchConcerts(condition, PageRequest.of(0, 10));

		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getTitle()).isEqualTo("테스트 콘서트");
	}

}
