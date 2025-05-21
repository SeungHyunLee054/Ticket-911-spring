package nbc.ticketing.ticket911.domain.concertseat.exception.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConcertSeatExceptionCode {

	INVALID_SEAT_SELECTION(false, HttpStatus.NOT_FOUND, "선택된 좌석이 존재하지 않습니다. 좌석 정보를 확인해주세요."),
	DIFFERENT_CONCERTS_NOT_ALLOWED(false, HttpStatus.BAD_REQUEST, "서로 다른 공연의 좌석은 동시에 예매할 수 없습니다."),
	SEAT_ALREADY_RESERVED(false, HttpStatus.CONFLICT, "이미 예약된 좌석입니다.");

	private final boolean success;
	private final HttpStatus status;
	private final String message;
}
