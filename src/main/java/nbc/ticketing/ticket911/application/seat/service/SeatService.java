package nbc.ticketing.ticket911.application.seat.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.application.stage.service.StageService;
import nbc.ticketing.ticket911.domain.seat.dto.request.CreateSeatRequestDto;
import nbc.ticketing.ticket911.domain.seat.dto.request.UpdateSeatRequestDto;
import nbc.ticketing.ticket911.domain.seat.dto.response.SeatResponseDto;
import nbc.ticketing.ticket911.domain.seat.entity.Seat;
import nbc.ticketing.ticket911.domain.seat.exception.SeatException;
import nbc.ticketing.ticket911.domain.seat.exception.code.SeatExceptionCode;
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
		Stage stage = stageService.getStageByStageIdOrElseThrow(stageId);

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
		Stage stage = stageService.getStageByStageIdOrElseThrow(stageId);
		List<Seat> seats = stage.getSeats();

		return seats.stream()
			.map(SeatResponseDto::from)
			.collect(Collectors.toList());
	}

	@SuppressWarnings("checkstyle:WhitespaceAround")
	@Transactional
	public SeatResponseDto updateSeat(Long stageId, Long seatId, UpdateSeatRequestDto updateSeatRequestDto) {
		Stage stage = stageService.getStageByStageIdOrElseThrow(stageId);
		Seat seat = getSeatBySeatIdOrElseThrow(seatId);

		if (seat.getStage() != stage) {
			throw new SeatException(SeatExceptionCode.SEAT_NOT_BELONG_TO_STAGE);
		}

		if (updateSeatRequestDto.getSeatName() != null) {
			seat.updateSeatName(updateSeatRequestDto.getSeatName());
		}

		if (updateSeatRequestDto.getSeatPrice() != null) {
			seat.updateSeatPrice(updateSeatRequestDto.getSeatPrice());
		}

		return SeatResponseDto.from(seat);

	}

	public void deleteSeat(Long stageId, Long seatId) {
		Stage stage = stageService.getStageByStageIdOrElseThrow(stageId);
		Seat seat = getSeatBySeatIdOrElseThrow(seatId);

		if (seat.getStage() != stage) {
			throw new SeatException(SeatExceptionCode.SEAT_NOT_BELONG_TO_STAGE);
		}

		seatRepository.deleteById(seatId);

	}

	private Seat getSeatBySeatIdOrElseThrow(Long seatId) {
		return seatRepository.findById(seatId)
			.orElseThrow(() -> new SeatException(SeatExceptionCode.SEAT_NOT_FOUND));
	}

}
