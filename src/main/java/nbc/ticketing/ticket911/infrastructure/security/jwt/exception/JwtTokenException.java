package nbc.ticketing.ticket911.infrastructure.security.jwt.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import nbc.ticketing.ticket911.common.exception.BaseException;
import nbc.ticketing.ticket911.infrastructure.security.jwt.exception.code.JwtTokenExceptionCode;

@Getter
public class JwtTokenException extends BaseException {
	private final JwtTokenExceptionCode errorCode;
	private final HttpStatus httpStatus;
	private final String message;

	public JwtTokenException(JwtTokenExceptionCode errorCode) {
		this.errorCode = errorCode;
		this.httpStatus = errorCode.getStatus();
		this.message = errorCode.getMessage();
	}
}
