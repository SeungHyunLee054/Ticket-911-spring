package nbc.ticketing.ticket911.domain.stage.dto.response;

import lombok.Builder;
import lombok.Getter;
import nbc.ticketing.ticket911.domain.stage.status.Status;

@Getter
@Builder
public class GetStageResponseDto {
	private String stageName;
	private Long totalSeats;
	private Status status;
}
