package nbc.ticketing.ticket911.domain.booking.exception.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookingExceptionCode {

	// 예매 실패 관련
	BOOKING_NOT_OPEN(false, HttpStatus.FORBIDDEN, "예매가 아직 시작되지 않았습니다."),
	BOOKING_CLOSED(false, HttpStatus.FORBIDDEN, "해당 공연은 예매가 마감되었습니다."),

	// 예약 접근 권한
	NOT_OWN_BOOKING(false, HttpStatus.FORBIDDEN, "본인의 예약이 아닙니다."),
	BOOKING_NOT_FOUND(false, HttpStatus.NOT_FOUND, "존재하지 않는 예약입니다."),

	// 취소 실패 관련
	ALREADY_CANCELED(false, HttpStatus.BAD_REQUEST, "이미 취소된 예약입니다."),
	CONCERT_STARTED_CANNOT_CANCEL(false, HttpStatus.FORBIDDEN, "공연 시작 후에는 예약을 취소할 수 없습니다.");

	private final boolean success;
	private final HttpStatus status;
	private final String message;
}
