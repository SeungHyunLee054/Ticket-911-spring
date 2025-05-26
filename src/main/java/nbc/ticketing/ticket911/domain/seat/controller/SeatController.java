package nbc.ticketing.ticket911.domain.seat.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nbc.ticketing.ticket911.common.response.CommonResponse;
import nbc.ticketing.ticket911.domain.seat.application.SeatService;
import nbc.ticketing.ticket911.domain.seat.dto.request.CreateSeatRequestDto;
import nbc.ticketing.ticket911.domain.seat.dto.request.UpdateSeatRequestDto;
import nbc.ticketing.ticket911.domain.seat.dto.response.SeatResponseDto;

@RestController
@RequestMapping("/stages/{stageId}/seats")
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

	@PatchMapping("/{seatId}")
	public ResponseEntity<CommonResponse<SeatResponseDto>> updateSeats(
		@PathVariable Long stageId,
		@PathVariable Long seatId,
		@RequestBody UpdateSeatRequestDto updateSeatRequestDto
	) {
		SeatResponseDto seatResponseDto = seatService.updateSeat(stageId, seatId, updateSeatRequestDto);

		return ResponseEntity.ok(CommonResponse.of(true, HttpStatus.OK.value(), "좌석 수정 성공", seatResponseDto));
	}

	@DeleteMapping("/{seatId}")
	public ResponseEntity<CommonResponse<Void>> deleteSeat(
		@PathVariable Long stageId,
		@PathVariable Long seatId
	) {
		seatService.deleteSeat(stageId, seatId);

		return ResponseEntity.ok(CommonResponse.of(true, HttpStatus.OK.value(), "좌석 삭제 완료"));
	}
}
