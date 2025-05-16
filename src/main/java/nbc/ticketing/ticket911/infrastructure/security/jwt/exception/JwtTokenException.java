package nbc.ticketing.ticket911.infrastructure.security.jwt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

import lombok.Getter;
import nbc.ticketing.ticket911.infrastructure.security.jwt.exception.code.JwtTokenExceptionCode;

@Getter
public class JwtTokenException extends AuthenticationException {
	private final JwtTokenExceptionCode errorCode;
	private final HttpStatus httpStatus;

	public JwtTokenException(JwtTokenExceptionCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
		this.httpStatus = errorCode.getStatus();
	}
}
