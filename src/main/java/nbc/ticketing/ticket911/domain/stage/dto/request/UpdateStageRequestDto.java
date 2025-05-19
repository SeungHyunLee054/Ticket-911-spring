package nbc.ticketing.ticket911.domain.stage.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nbc.ticketing.ticket911.domain.stage.status.StageStatus;

@Getter
@AllArgsConstructor
public class UpdateStageRequestDto {
	private String stageName;
	private StageStatus stageStatus;
}
