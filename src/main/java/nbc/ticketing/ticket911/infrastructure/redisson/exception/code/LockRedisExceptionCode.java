package nbc.ticketing.ticket911.infrastructure.redisson.exception.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LockRedisExceptionCode {

	LOCK_ACQUIRE_FAIL(false, HttpStatus.CONFLICT, "다른 사용자가 작업 중입니다. 잠시 후 다시 시도해주세요."),
	LOCK_TIMEOUT(false, HttpStatus.REQUEST_TIMEOUT, "락 획득 대기 시간이 초과되었습니다."),
	LOCK_KEY_EVALUATION_FAIL(false, HttpStatus.BAD_REQUEST, "락 키 평가 실패: 잘못된 SpEL 표현식입니다."),
	LOCK_INTERRUPTED(false, HttpStatus.INTERNAL_SERVER_ERROR, "락 획득 중 인터럽트가 발생했습니다."),
	LOCK_PROCEED_FAIL(false, HttpStatus.INTERNAL_SERVER_ERROR, "비즈니스 로직 실행 중 예외가 발생했습니다.");

	private final boolean success;
	private final HttpStatus status;
	private final String message;
}
