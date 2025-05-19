package nbc.ticketing.ticket911.domain.seat.service;

import org.springframework.stereotype.Component;

import nbc.ticketing.ticket911.domain.seat.dto.request.UpdateSeatRequestDto;
import nbc.ticketing.ticket911.domain.seat.entity.Seat;

@Component
public class SeatDomainService {
	public Seat updateSeat(Seat seat, UpdateSeatRequestDto updateSeatRequestDto) {
		if (updateSeatRequestDto.getSeatName() != null) {
			seat.updateSeatName(updateSeatRequestDto.getSeatName());
		}

		if (updateSeatRequestDto.getSeatPrice() != null) {
			seat.updateSeatPrice(updateSeatRequestDto.getSeatPrice());
		}

		return seat;

	}
}
