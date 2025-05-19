package nbc.ticketing.ticket911.domain.seat.dto.response;

import lombok.Builder;
import lombok.Getter;
import nbc.ticketing.ticket911.domain.seat.entity.Seat;

@Getter
@Builder
public class SeatResponseDto {
	private String seatName;
	private Long seatPrice;

	public static SeatResponseDto from(Seat seat) {
		return SeatResponseDto.builder()
			.seatName(seat.getSeatName())
			.seatPrice(seat.getSeatPrice())
			.build();
	}
}
