package nbc.ticketing.ticket911.domain.stage.exception.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StageExceptionCode {
	;

	private final boolean success;
	private final HttpStatus status;
	private final String message;
}
