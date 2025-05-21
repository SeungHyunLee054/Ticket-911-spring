package nbc.ticketing.ticket911.domain.stage.service;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.stage.entity.Stage;
import nbc.ticketing.ticket911.domain.stage.exception.StageException;
import nbc.ticketing.ticket911.domain.stage.exception.code.StageExceptionCode;
import nbc.ticketing.ticket911.domain.stage.repository.StageRepository;
import nbc.ticketing.ticket911.domain.stage.status.StageStatus;

@Component
@RequiredArgsConstructor
public class StageDomainService {

	private final StageRepository stageRepository;

	public Stage createStage(String stageName) {
		return Stage.builder()
			.stageName(stageName)
			.stageStatus(StageStatus.AVAILABLE)
			.totalSeat(0L)
			.build();
	}

	public Stage findStageByStageIdOrElseThrow(Long stageId) {
		return stageRepository.findById(stageId)
			.orElseThrow(() -> new StageException(StageExceptionCode.STAGE_NOT_FOUND));
	}
}
