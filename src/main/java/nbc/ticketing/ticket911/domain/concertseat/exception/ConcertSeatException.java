package nbc.ticketing.ticket911.domain.concertseat.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import nbc.ticketing.ticket911.common.exception.BaseException;
import nbc.ticketing.ticket911.domain.concertseat.exception.code.ConcertSeatExceptionCode;

@Getter
public class ConcertSeatException extends BaseException {
	private final ConcertSeatExceptionCode errorCode;
	private final HttpStatus httpStatus;
	private final String message;

	public ConcertSeatException(ConcertSeatExceptionCode errorCode) {
		this.errorCode = errorCode;
		this.httpStatus = errorCode.getStatus();
		this.message = errorCode.getMessage();
	}
}
