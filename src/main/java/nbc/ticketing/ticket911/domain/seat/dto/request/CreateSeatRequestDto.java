package nbc.ticketing.ticket911.domain.seat.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateSeatRequestDto {
	@NotNull(message = "좌석 이름은 필수 입력값입니다.")
	private String seatName;

	@NotNull(message = "좌석 가격은 필수 입력값입니다.")
	@Min(0)
	private Long seatPrice;

	@NotNull(message = "좌석 개수는 필수 입력값입니다.")
	@Min(1)
	private Long seatCount;
}
