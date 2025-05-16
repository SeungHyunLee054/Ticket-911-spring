package nbc.ticketing.ticket911.domain.seat.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import nbc.ticketing.ticket911.common.exception.BaseException;
import nbc.ticketing.ticket911.domain.seat.exception.code.SeatExceptionCode;

@Getter
public class SeatException extends BaseException {
	private final SeatExceptionCode errorCode;
	private final HttpStatus httpStatus;
	private final String message;

	public SeatException(SeatExceptionCode errorCode) {
		this.errorCode = errorCode;
		this.httpStatus = errorCode.getStatus();
		this.message = errorCode.getMessage();
	}
}
