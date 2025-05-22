package nbc.ticketing.ticket911.infrastructure.redisson.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import nbc.ticketing.ticket911.common.exception.BaseException;
import nbc.ticketing.ticket911.infrastructure.redisson.exception.code.LockRedisExceptionCode;

@Getter
public class LockRedisException extends BaseException {
	private final LockRedisExceptionCode errorCode;
	private final HttpStatus httpStatus;
	private final String message;

	public LockRedisException(LockRedisExceptionCode errorCode) {
		this.errorCode = errorCode;
		this.httpStatus = errorCode.getStatus();
		this.message = errorCode.getMessage();
	}
}
