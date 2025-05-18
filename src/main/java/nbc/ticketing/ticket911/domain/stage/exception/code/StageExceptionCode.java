package nbc.ticketing.ticket911.domain.stage.exception.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StageExceptionCode {
	STAGE_NOT_FOUND(false, HttpStatus.NOT_FOUND, "공연장이 존재하지 않습니다.");
	private final boolean success;
	private final HttpStatus status;
	private final String message;
}
