package nbc.ticketing.ticket911.domain.booking.exception.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookingExceptionCode {
	;

	private final boolean success;
	private final HttpStatus status;
	private final String message;
}
