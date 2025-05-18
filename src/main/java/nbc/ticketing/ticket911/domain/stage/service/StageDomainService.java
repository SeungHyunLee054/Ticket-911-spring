package nbc.ticketing.ticket911.domain.stage.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.stage.entity.Stage;
import nbc.ticketing.ticket911.domain.stage.exception.StageException;
import nbc.ticketing.ticket911.domain.stage.exception.code.StageExceptionCode;
import nbc.ticketing.ticket911.domain.stage.repository.StageRepository;

@Service
@RequiredArgsConstructor
public class StageDomainService {

	private final StageRepository stageRepository;

	public Stage verifyStage(Long stageId) {
		return stageRepository.findById(stageId)
			.orElseThrow(() -> new StageException(StageExceptionCode.STAGE_NOT_FOUND));
	}
}
