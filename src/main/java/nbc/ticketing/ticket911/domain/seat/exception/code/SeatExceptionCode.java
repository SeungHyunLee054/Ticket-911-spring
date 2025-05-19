package nbc.ticketing.ticket911.domain.seat.exception.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SeatExceptionCode {
	SEAT_NOT_FOUND(false, HttpStatus.NOT_FOUND, "좌석이 존재하지 않습니다."),
	SEAT_NOT_BELONG_TO_STAGE(false, HttpStatus.BAD_REQUEST, "해당 공연장에 소속된 좌석이 아닙니다.");

	private final boolean success;
	private final HttpStatus status;
	private final String message;
}
