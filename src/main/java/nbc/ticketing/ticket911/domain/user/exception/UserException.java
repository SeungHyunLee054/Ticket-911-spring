package nbc.ticketing.ticket911.domain.user.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import nbc.ticketing.ticket911.common.exception.BaseException;
import nbc.ticketing.ticket911.domain.user.exception.code.UserExceptionCode;

@Getter
public class UserException extends BaseException {
	private final UserExceptionCode errorCode;
	private final HttpStatus httpStatus;
	private final String message;

	public UserException(UserExceptionCode errorCode) {
		this.errorCode = errorCode;
		this.httpStatus = errorCode.getStatus();
		this.message = errorCode.getMessage();
	}
}
