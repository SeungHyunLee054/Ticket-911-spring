package nbc.ticketing.ticket911.domain.stage.dto.response;

import lombok.Builder;
import lombok.Getter;
import nbc.ticketing.ticket911.domain.stage.entity.Stage;
import nbc.ticketing.ticket911.domain.stage.status.StageStatus;

@Getter
@Builder
public class StageResponseDto {
	private String stageName;
	private Long totalSeats;
	private StageStatus stageStatus;

	public static StageResponseDto from(Stage stage) {
		return StageResponseDto.builder()
			.stageName(stage.getStageName())
			.totalSeats(stage.getTotalSeat())
			.stageStatus(stage.getStageStatus())
			.build();
	}
}
