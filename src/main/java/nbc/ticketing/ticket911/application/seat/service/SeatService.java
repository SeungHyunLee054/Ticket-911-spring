package nbc.ticketing.ticket911.application.seat.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.domain.seat.dto.request.CreateSeatRequestDto;
import nbc.ticketing.ticket911.domain.seat.dto.request.UpdateSeatRequestDto;
import nbc.ticketing.ticket911.domain.seat.dto.response.SeatResponseDto;
import nbc.ticketing.ticket911.domain.seat.entity.Seat;
import nbc.ticketing.ticket911.domain.seat.service.SeatDomainService;
import nbc.ticketing.ticket911.domain.stage.entity.Stage;
import nbc.ticketing.ticket911.domain.stage.service.StageDomainService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatService {

	private final SeatDomainService seatDomainService;
	private final StageDomainService stageDomainService;

	@Transactional
	public List<SeatResponseDto> createSeat(Long stageId, CreateSeatRequestDto createSeatRequestDto) {
		Stage stage = stageDomainService.findStageByStageIdOrElseThrow(stageId);

		List<Seat> seats = seatDomainService.createSeats(createSeatRequestDto, stage);

		seatDomainService.saveSeats(seats);

		stage.updateTotalSeat(createSeatRequestDto.getSeatCount());

		return seats.stream()
			.map(SeatResponseDto::from)
			.collect(Collectors.toList());

	}

	public List<SeatResponseDto> getSeats(Long stageId) {
		Stage stage = stageDomainService.findStageByStageIdOrElseThrow(stageId);
		List<Seat> seats = stage.getSeats();

		return seats.stream()
			.map(SeatResponseDto::from)
			.collect(Collectors.toList());
	}

	@Transactional
	public SeatResponseDto updateSeat(Long stageId, Long seatId, UpdateSeatRequestDto updateSeatRequestDto) {
		Stage stage = stageDomainService.findStageByStageIdOrElseThrow(stageId);
		Seat seat = seatDomainService.getSeatBySeatIdOrElseThrow(seatId);

		seatDomainService.verifySeat(seat, stage);

		seat = seatDomainService.updateSeat(seat, updateSeatRequestDto);

		return SeatResponseDto.from(seat);

	}

	@Transactional
	public void deleteSeat(Long stageId, Long seatId) {
		Stage stage = stageDomainService.findStageByStageIdOrElseThrow(stageId);
		Seat seat = seatDomainService.getSeatBySeatIdOrElseThrow(seatId);

		seatDomainService.verifySeat(seat, stage);

		seatDomainService.deleteSeat(seatId);

	}

}
