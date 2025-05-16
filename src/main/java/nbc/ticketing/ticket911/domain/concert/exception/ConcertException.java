package nbc.ticketing.ticket911.domain.concert.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import nbc.ticketing.ticket911.common.exception.BaseException;
import nbc.ticketing.ticket911.domain.concert.exception.code.ConcertExceptionCode;

@Getter
public class ConcertException extends BaseException {
	private final ConcertExceptionCode errorCode;
	private final HttpStatus httpStatus;
	private final String message;

	public ConcertException(ConcertExceptionCode errorCode) {
		this.errorCode = errorCode;
		this.httpStatus = errorCode.getStatus();
		this.message = errorCode.getMessage();
	}
}
