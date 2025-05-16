package nbc.ticketing.ticket911.domain.booking.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import nbc.ticketing.ticket911.common.exception.BaseException;
import nbc.ticketing.ticket911.domain.booking.exception.code.BookingExceptionCode;

@Getter
public class BookingException extends BaseException {
	private final BookingExceptionCode errorCode;
	private final HttpStatus httpStatus;
	private final String message;

	public BookingException(BookingExceptionCode errorCode) {
		this.errorCode = errorCode;
		this.httpStatus = errorCode.getStatus();
		this.message = errorCode.getMessage();
	}
}
