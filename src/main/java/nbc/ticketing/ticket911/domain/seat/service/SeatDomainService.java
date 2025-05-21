package nbc.ticketing.ticket911.domain.seat.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.seat.dto.request.CreateSeatRequestDto;
import nbc.ticketing.ticket911.domain.seat.dto.request.UpdateSeatRequestDto;
import nbc.ticketing.ticket911.domain.seat.entity.Seat;
import nbc.ticketing.ticket911.domain.seat.exception.SeatException;
import nbc.ticketing.ticket911.domain.seat.exception.code.SeatExceptionCode;
import nbc.ticketing.ticket911.domain.seat.repository.SeatRepository;
import nbc.ticketing.ticket911.domain.stage.entity.Stage;

@Component
@RequiredArgsConstructor
public class SeatDomainService {

	private final SeatRepository seatRepository;

	public Seat updateSeat(Seat seat, UpdateSeatRequestDto updateSeatRequestDto) {
		if (updateSeatRequestDto.getSeatName() != null) {
			seat.updateSeatName(updateSeatRequestDto.getSeatName());
		}

		if (updateSeatRequestDto.getSeatPrice() != null) {
			seat.updateSeatPrice(updateSeatRequestDto.getSeatPrice());
		}

		return seat;

	}

	public Seat getSeatBySeatIdOrElseThrow(Long seatId) {
		return seatRepository.findById(seatId)
			.orElseThrow(() -> new SeatException(SeatExceptionCode.SEAT_NOT_FOUND));
	}

	public List<Seat> createSeats(CreateSeatRequestDto createSeatRequestDto, Stage stage) {
		return LongStream.range(0, createSeatRequestDto.getSeatCount())
			.mapToObj(i -> {
				Seat seat = Seat.builder()
					.seatName(createSeatRequestDto.getSeatName())
					.seatPrice(createSeatRequestDto.getSeatPrice())
					.build();
				seat.addStage(stage);
				return seat;
			})
			.collect(Collectors.toList());
	}

	public void verifySeat(Seat seat, Stage stage) {
		if (seat.getStage() != stage) {
			throw new SeatException(SeatExceptionCode.SEAT_NOT_BELONG_TO_STAGE);
		}
	}
}
