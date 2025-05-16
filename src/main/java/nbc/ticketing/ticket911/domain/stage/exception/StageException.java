package nbc.ticketing.ticket911.domain.stage.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import nbc.ticketing.ticket911.common.exception.BaseException;
import nbc.ticketing.ticket911.domain.stage.exception.code.StageExceptionCode;

@Getter
public class StageException extends BaseException {
	private final StageExceptionCode errorCode;
	private final HttpStatus httpStatus;
	private final String message;

	public StageException(StageExceptionCode errorCode) {
		this.errorCode = errorCode;
		this.httpStatus = errorCode.getStatus();
		this.message = errorCode.getMessage();
	}
}
