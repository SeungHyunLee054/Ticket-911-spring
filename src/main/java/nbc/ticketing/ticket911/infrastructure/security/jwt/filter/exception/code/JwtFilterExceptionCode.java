package nbc.ticketing.ticket911.infrastructure.security.jwt.filter.exception.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtFilterExceptionCode {
	TOKEN_EXPIRED(false, HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
	EMPTY_TOKEN(false, HttpStatus.UNAUTHORIZED, "헤더에 토큰을 포함하고 있지 않습니다."),
	MALFORMED_JWT_REQUEST(false, HttpStatus.UNAUTHORIZED, "요청 형태가 잘못 되었습니다.");

	private final boolean success;
	private final HttpStatus status;
	private final String message;
}
