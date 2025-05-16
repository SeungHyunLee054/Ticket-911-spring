package nbc.ticketing.ticket911.infrastructure.security.jwt.filter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

import lombok.Getter;
import nbc.ticketing.ticket911.infrastructure.security.jwt.filter.exception.code.JwtFilterExceptionCode;

@Getter
public class JwtFilterException extends AuthenticationException {
	private final JwtFilterExceptionCode errorCode;
	private final HttpStatus httpStatus;

	public JwtFilterException(JwtFilterExceptionCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
		this.httpStatus = errorCode.getStatus();
	}
}
