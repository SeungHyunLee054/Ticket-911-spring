package nbc.ticketing.ticket911.application.seat.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.application.stage.service.StageService;
import nbc.ticketing.ticket911.domain.seat.dto.request.CreateSeatRequestDto;
import nbc.ticketing.ticket911.domain.seat.dto.response.SeatResponseDto;
import nbc.ticketing.ticket911.domain.seat.entity.Seat;
import nbc.ticketing.ticket911.domain.seat.repository.SeatRepository;
import nbc.ticketing.ticket911.domain.stage.entity.Stage;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatService {

	private final SeatRepository seatRepository;
	private final StageService stageService;

	@Transactional
	public List<SeatResponseDto> createSeat(Long stageId, CreateSeatRequestDto createSeatRequestDto) {
		Stage stage = stageService.verifyStage(stageId);

		List<Seat> seats = LongStream.range(0, createSeatRequestDto.getSeatCount())
			.mapToObj(i -> {
				Seat seat = Seat.builder()
					.seatName(createSeatRequestDto.getSeatName())
					.seatPrice(createSeatRequestDto.getSeatPrice())
					.build();
				seat.addStage(stage);
				return seat;
			})
			.collect(Collectors.toList());

		seatRepository.saveAll(seats);
		stage.updateTotalSeat(createSeatRequestDto.getSeatCount());

		return seats.stream()
			.map(SeatResponseDto::from)
			.collect(Collectors.toList());

	}

	public List<SeatResponseDto> getSeats(Long stageId) {
		Stage stage = stageService.verifyStage(stageId);
		List<Seat> seats = stage.getSeats();

		return seats.stream()
			.map(SeatResponseDto::from)
			.collect(Collectors.toList());
	}
}
