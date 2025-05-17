package nbc.ticketing.ticket911.application.stage.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.stage.dto.request.CreateStageRequestDto;
import nbc.ticketing.ticket911.domain.stage.dto.response.CreateStageResponseDto;
import nbc.ticketing.ticket911.domain.stage.dto.response.GetStageResponseDto;
import nbc.ticketing.ticket911.domain.stage.entity.Stage;
import nbc.ticketing.ticket911.domain.stage.repository.StageRepository;
import nbc.ticketing.ticket911.domain.stage.status.Status;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StageService {

	public final StageRepository stageRepository;

	@Transactional
	public CreateStageResponseDto createStage(CreateStageRequestDto createStageRequestDto) {
		Stage stage = Stage.builder()
			.stageName(createStageRequestDto.getStageName())
			.totalSeat(0L)
			.status(Status.AVAILABLE)
			.build();

		Stage savedStage = stageRepository.save(stage);

		return CreateStageResponseDto.builder()
			.stageName(savedStage.getStageName())
			.totalSeats(savedStage.getTotalSeat())
			.status(savedStage.getStatus())
			.build();
	}

	public Page<GetStageResponseDto> getStages(String keyword, Pageable pageable) {
		Page<Stage> stagePage = stageRepository.findByStageNameContaining(keyword, pageable);
		return stagePage.map(stage -> GetStageResponseDto.builder()
			.stageName(stage.getStageName())
			.totalSeats(stage.getTotalSeat())
			.status(stage.getStatus())
			.build());
	}
}
