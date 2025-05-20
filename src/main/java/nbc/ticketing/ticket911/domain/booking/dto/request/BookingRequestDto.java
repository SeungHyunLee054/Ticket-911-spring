package nbc.ticketing.ticket911.domain.booking.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookingRequestDto {

	@NotEmpty(message = "좌석은 1개 이상 선택해야 합니다.")
	@Size(max = 3, message = "좌석은 3개까지만 선택 가능합니다.")
	private final List<Long> seatIds;
}
