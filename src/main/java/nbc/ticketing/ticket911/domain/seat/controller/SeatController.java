package nbc.ticketing.ticket911.domain.seat.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.application.seat.service.SeatService;
import nbc.ticketing.ticket911.common.response.CommonResponse;
import nbc.ticketing.ticket911.domain.seat.dto.request.CreateSeatRequestDto;
import nbc.ticketing.ticket911.domain.seat.dto.response.SeatResponseDto;

@RestController
@RequestMapping("stages/{stageId}/seats")
@RequiredArgsConstructor
public class SeatController {

	private final SeatService seatService;

	@PostMapping
	public ResponseEntity<CommonResponse<List<SeatResponseDto>>> createSeat(
		@PathVariable Long stageId,
		@RequestBody @Valid CreateSeatRequestDto createSeatRequestDto
	) {
		List<SeatResponseDto> seatResponseDtos = seatService.createSeat(stageId, createSeatRequestDto);

		return ResponseEntity.ok(CommonResponse.of(true, HttpStatus.OK.value(), "좌석 생성 성공", seatResponseDtos));
	}

	@GetMapping
	public ResponseEntity<CommonResponse<List<SeatResponseDto>>> getSeats(
		@PathVariable Long stageId
	) {
		List<SeatResponseDto> seatResponseDtos = seatService.getSeats(stageId);

		return ResponseEntity.ok(CommonResponse.of(true, HttpStatus.OK.value(), "좌석 조회 성공", seatResponseDtos));
	}

}
