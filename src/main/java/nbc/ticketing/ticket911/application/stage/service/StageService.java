package nbc.ticketing.ticket911.application.stage.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.stage.dto.request.CreateStageRequestDto;
import nbc.ticketing.ticket911.domain.stage.dto.request.UpdateStageRequestDto;
import nbc.ticketing.ticket911.domain.stage.dto.response.StageResponseDto;
import nbc.ticketing.ticket911.domain.stage.entity.Stage;
import nbc.ticketing.ticket911.domain.stage.exception.StageException;
import nbc.ticketing.ticket911.domain.stage.exception.code.StageExceptionCode;
import nbc.ticketing.ticket911.domain.stage.repository.StageRepository;
import nbc.ticketing.ticket911.domain.stage.service.StageDomainService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StageService {

	private final StageRepository stageRepository;
	private final StageDomainService stageDomainService;

	@Transactional
	public StageResponseDto createStage(CreateStageRequestDto createStageRequestDto) {
		Stage stage = stageDomainService.createStage(createStageRequestDto.getStageName());

		Stage savedStage = stageRepository.save(stage);

		return StageResponseDto.from(savedStage);
	}

	public Page<StageResponseDto> getStages(String keyword, Pageable pageable) {
		Page<Stage> stagePage = stageRepository.findByStageNameContaining(keyword, pageable);

		return stagePage.map(StageResponseDto::from);
	}

	public StageResponseDto getStage(Long stageId) {
		Stage stage = findStageByStageIdOrElseThrow(stageId);

		return StageResponseDto.from(stage);
	}

	@Transactional
	public StageResponseDto updateStage(Long stageId, UpdateStageRequestDto updateStageRequestDto) {
		Stage stage = findStageByStageIdOrElseThrow(stageId);

		if (updateStageRequestDto.getStageName() != null) {
			stage.updateStageName(updateStageRequestDto.getStageName());
		}
		if (updateStageRequestDto.getStageStatus() != null) {
			stage.updateStageStatus(updateStageRequestDto.getStageStatus());
		}

		return StageResponseDto.from(stage);
	}

	public Stage findStageByStageIdOrElseThrow(Long stageId) {
		return stageRepository.findById(stageId)
			.orElseThrow(() -> new StageException(StageExceptionCode.STAGE_NOT_FOUND));
	}
}
