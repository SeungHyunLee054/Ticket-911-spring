package nbc.ticketing.ticket911.domain.seat.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateSeatRequestDto {
	private String seatName;

	@Min(0)
	private Long seatPrice;
}
