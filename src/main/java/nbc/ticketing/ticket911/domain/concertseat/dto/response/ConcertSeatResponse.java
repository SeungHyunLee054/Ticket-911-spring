package nbc.ticketing.ticket911.domain.concertseat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nbc.ticketing.ticket911.domain.concertseat.entity.ConcertSeat;

@Getter
@AllArgsConstructor
public class ConcertSeatResponse {
	Long seatId;
	String seatName;
	boolean isReserved;

	public static ConcertSeatResponse from(ConcertSeat seat) {
		return new ConcertSeatResponse(seat.getSeat().getId(), seat.getSeat().getSeatName(), seat.isReserved());
	}
}
