package nbc.ticketing.ticket911.domain.stage.service;

import org.springframework.stereotype.Component;

import nbc.ticketing.ticket911.domain.stage.entity.Stage;
import nbc.ticketing.ticket911.domain.stage.status.StageStatus;

@Component
public class StageDomainService {

	public Stage createStage(String stageName) {
		return Stage.builder()
			.stageName(stageName)
			.stageStatus(StageStatus.AVAILABLE)
			.totalSeat(0L)
			.build();
	}
}
