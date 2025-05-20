package nbc.ticketing.ticket911.domain.concertseat.exception.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConcertSeatExceptionCode {
	ALREADY_BOOKED(false, HttpStatus.BAD_REQUEST, "이미 예약된 좌석입니다."),
	CONCERT_SEAT_NOT_FOUND(false, HttpStatus.CONFLICT, "공연 좌석 없음");
	private final boolean success;
	private final HttpStatus status;
	private final String message;
}
