package nbc.ticketing.ticket911.domain.concertseat.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConcertSeatReserveRequest {
	@NotBlank
	private Long seatId;
}
