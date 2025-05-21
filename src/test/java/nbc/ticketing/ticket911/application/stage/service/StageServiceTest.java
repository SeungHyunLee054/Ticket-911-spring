package nbc.ticketing.ticket911.application.stage.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import nbc.ticketing.ticket911.domain.stage.dto.request.CreateStageRequestDto;
import nbc.ticketing.ticket911.domain.stage.dto.response.StageResponseDto;
import nbc.ticketing.ticket911.domain.stage.entity.Stage;
import nbc.ticketing.ticket911.domain.stage.repository.StageRepository;
import nbc.ticketing.ticket911.domain.stage.status.StageStatus;
import nbc.ticketing.ticket911.domain.user.constant.UserRole;
import nbc.ticketing.ticket911.domain.user.entity.User;

@ExtendWith(MockitoExtension.class)
class StageServiceTest {

	@Mock
	private StageRepository stageRepository;

	@InjectMocks
	private StageService stageService;

	@Spy
	private User user;

	@BeforeEach
	void setUp() {
		user = User.builder()
			.id(1L)
			.email("a@a.com")
			.password("a1A@pass")
			.nickname("admin")
			.roles(Set.of(UserRole.ROLE_ADMIN))
			.point(0)
			.build();
	}

	@Nested
	@DisplayName("공연장 생성 테스트")
	class CreateStageTest {

		@Test
		@DisplayName("공연장 생성 성공")
		void success_createStage() {
			// Given
			CreateStageRequestDto createStageRequestDto = new CreateStageRequestDto("test");

			Stage savedStage = Stage.builder()
				.id(1L)
				.stageName("test")
				.totalSeat(0L)
				.stageStatus(StageStatus.AVAILABLE)
				.build();

			when(stageRepository.save(any(Stage.class))).thenReturn(savedStage);

			// When
			StageResponseDto stageResponseDto = stageService.createStage(createStageRequestDto);

			// Then
			assertThat(stageResponseDto.getStageName()).isEqualTo("test");
			assertThat(stageResponseDto.getStageStatus()).isEqualTo(StageStatus.AVAILABLE);
			assertThat(stageResponseDto.getTotalSeats()).isEqualTo(0L);

			verify(stageRepository, times(1)).save(any(Stage.class));

		}
	}

	@Nested
	@DisplayName("공연장 조회 테스트")
	class UpdateStageTest {
		@Test
		@DisplayName("공연장 조회 성공")
		void success_getStages() {
			String keyword = "Main";
			Pageable pageable = PageRequest.of(0, 10);

			List<Stage> stages = List.of(
				Stage.builder().id(1L).stageName("Main Stage 1").build(),
				Stage.builder().id(2L).stageName("Main Stage 2").build()
			);
			Page<Stage> stagePage = new PageImpl<>(stages, pageable, stages.size());

			when(stageRepository.findByStageNameContaining(keyword, pageable)).thenReturn(stagePage);

			// when
			Page<StageResponseDto> result = stageService.getStages(keyword, pageable);

			// then
			assertThat(result.getTotalElements()).isEqualTo(stages.size());
			assertThat(result.getContent())
				.extracting(StageResponseDto::getStageName)
				.containsExactly("Main Stage 1", "Main Stage 2");

			verify(stageRepository, times(1)).findByStageNameContaining(keyword, pageable);
		}
	}

}
