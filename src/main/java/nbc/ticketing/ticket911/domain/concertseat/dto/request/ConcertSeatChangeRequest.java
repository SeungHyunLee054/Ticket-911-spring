package nbc.ticketing.ticket911.domain.concertseat.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConcertSeatChangeRequest {
	@NotNull
	private Long oldSeatId;

	@NotNull
	private Long newSeatId;
}
